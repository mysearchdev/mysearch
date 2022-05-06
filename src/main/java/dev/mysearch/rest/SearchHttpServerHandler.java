package dev.mysearch.rest;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.common.Json;
import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.document.DocumentAddEndpoint;
import dev.mysearch.rest.endpont.document.DocumentDeleteByIdEndpoint;
import dev.mysearch.rest.endpont.document.DocumentGetByIdEndpoint;
import dev.mysearch.rest.endpont.index.IndexCreateEndpoint;
import dev.mysearch.rest.endpont.index.IndexDropEndpoint;
import dev.mysearch.rest.endpont.server.ServerInfoEndpoint;
import dev.mysearch.rest.endpont.server.ServerPingEndpoint;
import dev.mysearch.rest.model.RestResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Sharable
public class SearchHttpServerHandler extends SimpleChannelInboundHandler<Object> implements InitializingBean {

	@Autowired
	private ServerInfoEndpoint serverInfoEndpoint;

	@Autowired
	private ServerPingEndpoint serverPingEndpoint;

	@Autowired
	private IndexCreateEndpoint indexCreateEndpoint;

	@Autowired
	private IndexDropEndpoint indexDropEndpoint;

	@Autowired
	private DocumentAddEndpoint documentAddEndpoint;

	@Autowired
	private DocumentGetByIdEndpoint documentGetByIdEndpoint;
	
	@Autowired
	private DocumentDeleteByIdEndpoint documentDeleteByIdEndpoint;

	private Map<String, AbstractRestEndpoint> endpoints;

	@Override
	public void afterPropertiesSet() throws Exception {

		endpoints = Map.of( //

				// Server
				"/api/server/info", serverInfoEndpoint, //
				"/api/server/ping", serverPingEndpoint, //

				// Index
				"/api/index/create", indexCreateEndpoint, //
				"/api/index/drop", indexDropEndpoint, //

				// Document
				"/api/document/add", documentAddEndpoint, //
				"/api/document/get", documentGetByIdEndpoint, //
				"/api/document/delete", documentDeleteByIdEndpoint //

		);

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

		log.debug("Msg: " + msg.toString());

		if (msg instanceof HttpRequest) {

			var req = (HttpRequest) msg;

			var dec = new QueryStringDecoder(req.uri());

			var endpoint = this.endpoints.get(dec.rawPath());

			if (endpoint != null) {

				// Is http method ok?
				if (false == req.method().equals(endpoint.getMethod())) {

					var error = new RestResponse<Boolean>();
					error.setError(true);
					error.setErrorMessage("HTTP method to used for this endpoint is " + endpoint.getMethod());

					var responseBytes = Json.writeValueAsBytes(error);
					writeResponse(ctx, req, responseBytes, HttpResponseStatus.METHOD_NOT_ALLOWED);

					return;
				}

				byte[] responseBytes = new byte[0];

				try {

					var respData = endpoint.service(req, dec);
					var resp = RestResponse.of(respData);

					responseBytes = Json.writeValueAsBytes(resp);

					writeResponse(ctx, req, responseBytes, HttpResponseStatus.OK);

				} catch (MySearchException e) {

					var error = new RestResponse<Boolean>();
					error.setError(true);
					error.setErrorMessage(e.getMessage());

					responseBytes = Json.writeValueAsBytes(error);
					writeResponse(ctx, req, responseBytes, HttpResponseStatus.INTERNAL_SERVER_ERROR);
					
				} catch (Exception e) {

					log.error("Error: ", e);

					var error = new RestResponse<Boolean>();
					error.setError(true);
					error.setErrorMessage(e.getMessage());

					responseBytes = Json.writeValueAsBytes(error);
					writeResponse(ctx, req, responseBytes, HttpResponseStatus.INTERNAL_SERVER_ERROR);

				}

			} else {
				// 404
				error404(ctx, req);
			}

		}

	}

	private void writeResponse(ChannelHandlerContext ctx, HttpRequest req, byte[] responseBytes,
			HttpResponseStatus status) {

		if (HttpUtil.is100ContinueExpected(req)) {
			ctx.write(new DefaultFullHttpResponse(req.protocolVersion(), CONTINUE));
		}
		boolean keepAlive = HttpUtil.isKeepAlive(req);
		FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), status,
				Unpooled.wrappedBuffer(responseBytes));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseBytes.length);

		if (!keepAlive) {
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.write(response);
		}

	}

	private void error404(ChannelHandlerContext ctx, HttpRequest req) {

		var error = new RestResponse<Boolean>();
		error.setError(true);
		error.setErrorMessage("Not found");

		var responseBytes = Json.writeValueAsBytes(error);
		writeResponse(ctx, req, responseBytes, HttpResponseStatus.NOT_FOUND);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
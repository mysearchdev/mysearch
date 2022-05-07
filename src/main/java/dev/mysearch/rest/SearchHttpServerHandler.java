package dev.mysearch.rest;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;

import java.util.regex.Pattern;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.common.Json;
import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import dev.mysearch.rest.endpont.document.DocumentAddEndpoint;
import dev.mysearch.rest.endpont.document.DocumentDeleteByIdEndpoint;
import dev.mysearch.rest.endpont.document.DocumentGetByIdEndpoint;
import dev.mysearch.rest.endpont.document.DocumentsSearchEndpoint;
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
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
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
	
	@Autowired
	private DocumentsSearchEndpoint documentsSearchEndpoint;

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

		var req = (HttpRequest) msg;

		if (msg instanceof HttpRequest) {

			var dec = new QueryStringDecoder(req.uri());

			try {

				final var endpointContext = new RestEndpointContext();
				endpointContext.setReq(req);
				endpointContext.setDec(dec);

				var endpoint = findEnpoint(req, dec, endpointContext);

				if (endpoint == null) {
					error(ctx, req, "Endpoint not found", HttpResponseStatus.NOT_FOUND);
					return;
				}

				if (false == req.method().equals(endpoint.getMethod())) {
					error(ctx, req, "HTTP method to used for this endpoint is " + endpoint.getMethod(),
							HttpResponseStatus.METHOD_NOT_ALLOWED);
					return;
				}

				final var endpointResult = endpoint.service(endpointContext);

				var resp = RestResponse.of(endpointResult);

				writeResponse(ctx, req, resp, HttpResponseStatus.OK);

			} catch (Exception e) {

				if (false == e instanceof MySearchException) {
					log.error("Error: ", e);
				}

				error(ctx, req, e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);

			}

		} else {
			// 404
			error(ctx, req, "Endpoint not found", HttpResponseStatus.NOT_FOUND);
		}

	}

	private final Pattern IndexPattern = Pattern.compile("^/[a-z0-9_]+");
	
	private final Pattern DocumentGetOrDeletePattern = Pattern.compile("^/[a-z0-9_]+/document/(.*)$");
	
	private final Pattern DocumentAddPattern = Pattern.compile("^/[a-z0-9_]+/document$");
	
	private final Pattern DocumentSearchPattern = Pattern.compile("^/[a-z0-9_]+/search$");

	private AbstractRestEndpoint findEnpoint(HttpRequest req, QueryStringDecoder dec, RestEndpointContext endpointContext) {

		// Extract index name
		var rawPath = dec.rawPath();

		log.debug("Raw path: " + rawPath);

		if (rawPath.equals("/_/server/info"))
			return this.serverInfoEndpoint;

		if (rawPath.equals("/_/server/ping"))
			return this.serverPingEndpoint;
		
		// Search documents
		{
			var matcher = DocumentSearchPattern.matcher(rawPath);
			if (matcher.matches()) {
				return this.documentsSearchEndpoint;
			}
		}

		// Index operations?
		{
			var matcher = IndexPattern.matcher(rawPath);
			if (matcher.matches()) {
				if (req.method() == HttpMethod.DELETE) {
					return this.indexDropEndpoint;
				} else if (req.method() == HttpMethod.POST) {
					return this.indexCreateEndpoint;
				}
			}
		}
		
		// Document add?
		
		{
			final var matcher = DocumentAddPattern.matcher(rawPath);
			if (matcher.matches()) {
				if (req.method() == HttpMethod.PUT) {
					return this.documentAddEndpoint;
				}
			}
			
		}
		
		// Document get by id or delete by id?
		{
			final var matcher = DocumentGetOrDeletePattern.matcher(rawPath);
			if (matcher.matches()) {
				
				endpointContext.setDocumentId(matcher.group(1));
				
				if (req.method() == HttpMethod.DELETE) {
					return this.documentDeleteByIdEndpoint;
				} else if (req.method() == HttpMethod.GET) {
					return this.documentGetByIdEndpoint;
				}
			}
			
		}
		
		return null;

	}

	private void writeResponse(ChannelHandlerContext ctx, HttpRequest req, byte[] responseBytes,
			HttpResponseStatus status) {

		if (HttpUtil.is100ContinueExpected(req)) {
			ctx.write(new DefaultFullHttpResponse(req.protocolVersion(), CONTINUE));
		}

		var keepAlive = HttpUtil.isKeepAlive(req);

		var response = new DefaultFullHttpResponse(req.protocolVersion(), status,
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

	private void error(ChannelHandlerContext ctx, HttpRequest req, String message, HttpResponseStatus status) {
		final var error = new RestResponse<Boolean>();
		error.setError(true);
		error.setErrorMessage(message);
		writeResponse(ctx, req, error, status);
	}

	private void writeResponse(ChannelHandlerContext ctx, HttpRequest req, RestResponse resp,
			HttpResponseStatus status) {
		var responseBytes = Json.writeValueAsBytes(resp);
		writeResponse(ctx, req, responseBytes, status);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
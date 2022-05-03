package dev.mysearch.rest;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

class RequestUtils {

	static StringBuilder formatParams(HttpRequest request) {
		var responseData = new StringBuilder();
		var queryStringDecoder = new QueryStringDecoder(request.uri());
		var params = queryStringDecoder.parameters();
		if (!params.isEmpty()) {
			for (var p : params.entrySet()) {
				var key = p.getKey();
				var vals = p.getValue();
				for (var val : vals) {
					responseData.append("Parameter: ").append(key.toUpperCase()).append(" = ").append(val.toUpperCase())
							.append("\r\n");
				}
			}
			responseData.append("\r\n");
		}
		return responseData;
	}

	static StringBuilder formatBody(HttpContent httpContent) {
		var responseData = new StringBuilder();
		var content = httpContent.content();
		if (content.isReadable()) {
			responseData.append(content.toString(CharsetUtil.UTF_8).toUpperCase());
			responseData.append("\r\n");
		}
		return responseData;
	}

	static StringBuilder evaluateDecoderResult(HttpObject o) {
		var responseData = new StringBuilder();
		var result = o.decoderResult();

		if (!result.isSuccess()) {
			responseData.append("..Decoder Failure: ");
			responseData.append(result.cause());
			responseData.append("\r\n");
		}

		return responseData;
	}

	static StringBuilder prepareLastResponse(HttpRequest request, LastHttpContent trailer) {

		var responseData = new StringBuilder();
		responseData.append("Good Bye!\r\n");

		if (!trailer.trailingHeaders().isEmpty()) {
			responseData.append("\r\n");
			for (var name : trailer.trailingHeaders().names()) {
				for (var value : trailer.trailingHeaders().getAll(name)) {
					responseData.append("P.S. Trailing Header: ");
					responseData.append(name).append(" = ").append(value).append("\r\n");
				}
			}
			responseData.append("\r\n");
		}
		return responseData;
	}

}
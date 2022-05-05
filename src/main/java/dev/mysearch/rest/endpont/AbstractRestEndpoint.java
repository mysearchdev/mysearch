package dev.mysearch.rest.endpont;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import dev.mysearch.common.Json;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public abstract class AbstractRestEndpoint<T> {

	public abstract T service(HttpRequest req, QueryStringDecoder dec) throws MySearchException, Exception;

	public abstract HttpMethod getMethod();

	protected static <E> E getRequestBodyAsObject(HttpRequest req, Class<E> c) {

		if (req instanceof FullHttpRequest) {
			var json = ((io.netty.handler.codec.http.FullHttpRequest) req).content()
					.toString(java.nio.charset.StandardCharsets.UTF_8);
			try {
				return Json.getMapper().readValue(json, c);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	protected String getRequestBody(HttpRequest req) {
		if (req instanceof FullHttpRequest) {
			return ((io.netty.handler.codec.http.FullHttpRequest) req).content()
					.toString(java.nio.charset.StandardCharsets.UTF_8);
		} else {
			return StringUtils.EMPTY;
		}

	}
}

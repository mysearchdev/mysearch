package dev.mysearch.rest.endpont;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public abstract class AbstractRestEndpoint<T> {
	public abstract T service(HttpRequest req, QueryStringDecoder dec) throws Exception;
}

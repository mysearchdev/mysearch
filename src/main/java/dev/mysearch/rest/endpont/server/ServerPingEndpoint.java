package dev.mysearch.rest.endpont.server;

import org.springframework.stereotype.Service;

import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

@Service
public class ServerPingEndpoint extends AbstractRestEndpoint<String> {

	final static String ok = "ok";
	
	@Override
	public String service(HttpRequest req, QueryStringDecoder dec) {
		return ok;
	}

}

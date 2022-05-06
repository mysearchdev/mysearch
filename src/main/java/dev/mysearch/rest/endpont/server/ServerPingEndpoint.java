package dev.mysearch.rest.endpont.server;

import org.springframework.stereotype.Service;

import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import io.netty.handler.codec.http.HttpMethod;

@Service
public class ServerPingEndpoint extends AbstractRestEndpoint<String> {

	final static String ok = "ok";
	
	@Override
	public String service(RestEndpointContext ctx) throws MySearchException, Exception {
		return ok;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.GET;
	}

}

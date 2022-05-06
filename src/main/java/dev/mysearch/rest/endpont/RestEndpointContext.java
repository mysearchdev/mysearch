package dev.mysearch.rest.endpont;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Data;

@Data
public class RestEndpointContext {

	private HttpRequest req;

	private QueryStringDecoder dec;
	
	private String requestBody;

	public String getIndexName() {
		final var path = this.dec.rawPath();
		final var lastPathSeparator = path.indexOf('/', 1);

		if (lastPathSeparator == -1) {
			return path.substring(1);
		} else {
			return path.substring(1, lastPathSeparator);
		}

	}

}

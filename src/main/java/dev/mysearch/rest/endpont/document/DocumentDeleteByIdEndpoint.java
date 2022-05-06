package dev.mysearch.rest.endpont.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import dev.mysearch.search.IndexService;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DocumentDeleteByIdEndpoint extends AbstractRestEndpoint<Boolean> {

	@Autowired
	private IndexService indexService;

	@Override
	public Boolean service(RestEndpointContext ctx) throws MySearchException, Exception {

		final var index = indexService.getExistingIndex(ctx.getIndexName());

		index.deleteById(ctx.getDocumentId());

		return true;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.DELETE;
	}

}

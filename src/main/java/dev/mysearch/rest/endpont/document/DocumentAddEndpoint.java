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
public class DocumentAddEndpoint extends AbstractRestEndpoint<Boolean> {

	@Autowired
	private IndexService indexService;

	@Override
	public Boolean service(RestEndpointContext ctx) throws MySearchException, Exception {

		var text = getRequestBody(ctx.getReq());

		log.debug("Add doc: " + text);

		indexService.submitToIndexAsync(ctx.getIndexName(), ctx.getDocumentId(), text);

		return true;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.PUT;
	}

}

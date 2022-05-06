package dev.mysearch.rest.endpont.index;

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
public class IndexCreateEndpoint extends AbstractRestEndpoint<Boolean> {

	@Autowired
	private IndexService indexService;

	@Override
	public Boolean service(RestEndpointContext ctx) throws MySearchException, Exception {
		
		log.debug("Index name: " + ctx.getIndexName());
		
		indexService.createIndex(ctx.getIndexName());
		return true;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.POST;
	}

}

package dev.mysearch.rest.endpont.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import dev.mysearch.search.IndexService;
import dev.mysearch.search.IndexService.IndexInfo;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IndexGetEndpoint extends AbstractRestEndpoint<IndexInfo> {

	private final String ParamLang = "lang";
	private final String English = "en";

	@Autowired
	private IndexService indexService;

	@Override
	public IndexInfo service(RestEndpointContext ctx) throws MySearchException, Exception {

		log.debug("GET index info" + ctx.getIndexName());

		return indexService.getIndexInfo(ctx.getIndexName());

	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.GET;
	}

}

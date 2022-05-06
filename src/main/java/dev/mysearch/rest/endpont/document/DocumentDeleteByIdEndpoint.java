package dev.mysearch.rest.endpont.document;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.search.IndexService;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DocumentDeleteByIdEndpoint extends AbstractRestEndpoint<Boolean> {

	@Autowired
	private IndexService indexService;

	@Override
	public Boolean service(HttpRequest req, QueryStringDecoder dec) throws MySearchException, Exception {

		var indexNames = dec.parameters().get("index");

		if (CollectionUtils.isEmpty(indexNames)) {
			throw new MySearchException("Please, specify a 'index' parameter (index name)");
		}
		var ids = dec.parameters().get("id");

		if (CollectionUtils.isEmpty(ids)) {
			throw new MySearchException("Please, specify an 'id' parameter");
		}

		var indexName = indexNames.get(0);
		var id = ids.get(0);

		var ctx = indexService.getIndexContext(indexName);

		ctx.deleteById(id);

		return true;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.DELETE;
	}

}

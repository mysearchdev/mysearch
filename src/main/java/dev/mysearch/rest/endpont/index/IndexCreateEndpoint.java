package dev.mysearch.rest.endpont.index;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.search.IndexService;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IndexCreateEndpoint extends AbstractRestEndpoint<Boolean> {

	@Autowired
	private IndexService indexService;

	@Override
	public Boolean service(HttpRequest req, QueryStringDecoder dec) throws Exception {

		var indexName = dec.parameters().get("name");

		if (CollectionUtils.isEmpty(indexName)) {
			throw new Exception("Please, specify a 'name' parameter (index name)");
		}

		indexService.createIndex(indexName.get(0));

		return true;
	}

}

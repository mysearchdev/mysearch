package dev.mysearch.rest.endpont.document;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.index.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.model.MySearchDocument;
import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.search.IndexService;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DocumentAddEndpoint extends AbstractRestEndpoint<Boolean> {

	@Autowired
	private IndexService indexService;

	@Override
	public Boolean service(HttpRequest req, QueryStringDecoder dec) throws MySearchException, Exception {

		MySearchDocument doc = super.getRequestBodyAsObject(req, MySearchDocument.class);

		log.debug("Add doc: " + doc);

		var indexNames = dec.parameters().get("index");

		if (CollectionUtils.isEmpty(indexNames)) {
			throw new MySearchException("Please, specify a 'index' parameter (index name)");
		}

		var indexName = indexNames.get(0);

		var ctx = indexService.getIndexContext(indexName);

//		ctx.getIndexWriter().addDocument(doc.toLuceneDocument());
		
		ctx.updateDocument(new Term(MySearchDocument.DOC_ID, doc.getId()), doc.toLuceneDocument());
		
		ctx.commit();

		return true;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.PUT;
	}

}

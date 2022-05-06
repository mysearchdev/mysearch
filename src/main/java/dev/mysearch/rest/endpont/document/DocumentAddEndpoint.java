package dev.mysearch.rest.endpont.document;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.model.MySearchDocument;
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

		MySearchDocument doc = getRequestBodyAsObject(ctx.getReq(), MySearchDocument.class);

		log.debug("Add doc: " + doc);

		var index = indexService.getIndexContext(ctx.getIndexName(), OpenMode.APPEND);

		index.updateDocument(new Term(MySearchDocument.DOC_ID, doc.getId()), doc.toLuceneDocument());

		index.commit();

		return true;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.PUT;
	}

}

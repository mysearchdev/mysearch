package dev.mysearch.rest.endpont.document;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
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
public class DocumentGetByIdEndpoint extends AbstractRestEndpoint<MySearchDocument> {

	@Autowired
	private IndexService indexService;

	@Override
	public MySearchDocument service(RestEndpointContext ctx) throws MySearchException, Exception {

		var index = indexService.getExistingIndex(ctx.getIndexName());

		var term = new Term(MySearchDocument.DOC_ID, ctx.getDocumentId());
		var q = new TermQuery(term);
		var searcher = index.getSearcher();

		TopDocs docs = searcher.search(q, 1);

		if (docs.totalHits.value > 0) {

			var doc = searcher.doc(docs.scoreDocs[0].doc);

			return MySearchDocument.from(doc, ctx.getDocumentId());

		}

		return null;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.GET;
	}

}

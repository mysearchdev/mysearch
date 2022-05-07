package dev.mysearch.rest.endpont.document;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
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
public class DocumentsSearchEndpoint extends AbstractRestEndpoint<MySearchDocument> {

	@Autowired
	private IndexService indexService;

	@Override
	public MySearchDocument service(RestEndpointContext ctx) throws MySearchException, Exception {

		final var index = indexService.getExistingIndex(ctx.getIndexName());

		var queries = ctx.getDec().parameters().get("q");
		var fields = ctx.getDec().parameters().get("field");

		String query = "";
		String field = "";

		if (false == CollectionUtils.isEmpty(queries)) {
			query = queries.get(0);
		}
		if (false == CollectionUtils.isEmpty(fields)) {
			field = fields.get(0);
		}

		log.debug(field + ": " + query);

		var parser = new QueryParser(field, index.getAnalyzer());

		log.debug("Parser: " + parser.toString());

		var searcher = index.getSearcher();

		try {
			var q = parser.parse(query);

			log.debug("Q: " + q);

			TopDocs docs = searcher.search(q, 1);

			if (docs.totalHits.value > 0) {

				var doc = searcher.doc(docs.scoreDocs[0].doc);

				return MySearchDocument.from(doc, ctx.getDocumentId(), docs.scoreDocs[0].score);

			}

		} catch (ParseException e) {
			log.error("Error: ", e);
		}

		return null;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.GET;
	}

}

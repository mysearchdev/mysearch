package dev.mysearch.rest.endpont.document;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mysearch.model.MySearchDocument;
import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import dev.mysearch.search.IndexService;
import dev.mysearch.search.SearchIndex;
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

		var index = indexService.getExistingIndex(ctx.getIndexName());

		var doc = new Document();
		
		var f = new StringField(MySearchDocument.DOC_ID, ctx.getDocumentId(), Field.Store.YES);
		doc.add(f);
		
		doc.add(new TextField(MySearchDocument.TEXT_ID, text, Field.Store.YES));
		
		var date = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date());
		doc.add(new StringField(MySearchDocument.DATE_ID, date, Field.Store.YES));
		
		index.updateDocument(new Term(MySearchDocument.DOC_ID, ctx.getDocumentId()), doc);

		index.commit();

		return true;
	}
	
	@Override
	public HttpMethod getMethod() {
		return HttpMethod.PUT;
	}

}

/**

Copyright (C) 2022 MySearch.Dev contributors (dev@mysearch.dev) 
Copyright (C) 2022 Sergey Nechaev (serg.nechaev@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 

*/

package dev.mysearch.rest.endpont.document;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import dev.mysearch.common.JsonHelper;
import dev.mysearch.model.MySearchDocument;
import dev.mysearch.model.MySearchQuery;
import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import dev.mysearch.search.IndexService;
import dev.mysearch.search.SearchIndex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DocumentsSearchEndpoint extends AbstractRestEndpoint<DocumentsSearchEndpoint.MySearchResultWrapper> {

	@Autowired
	private IndexService indexService;

	@Data
	@AllArgsConstructor
	@JsonInclude(Include.NON_NULL)
	public static class MySearchResult {
		private String id;
		private float score;
		private String date;
		private String text;
	}

	@Data
	@JsonInclude(Include.NON_NULL)
	public static class MySearchResultWrapper {
		private int size;
		private String index;
		private List<MySearchResult> results;
	}

	@Override
	public MySearchResultWrapper service(RestEndpointContext ctx) throws MySearchException, Exception {

		final var index = indexService.getExistingIndex(ctx.getIndexName());

		final var userQuery = JsonHelper.getMapper().readValue(ctx.getRequestBody(), MySearchQuery.class);

		var result = new MySearchResultWrapper();
		result.setIndex(ctx.getIndexName());

		/*
		var query = ctx.getParameter("q", "");
		var max = NumberUtils.toInt(ctx.getParameter("max", "100"), 100);
		var hilite = BooleanUtils.toBoolean(ctx.getParameter("hilite", "false"));
		var onlyId = BooleanUtils.toBoolean(ctx.getParameter("onlyId", "false"));

		var parser = new QueryParser(MySearchDocument.TEXT_ID, index.getAnalyzer());

		var searcher = index.getSearcher();


		try {
			var q = parser.parse(query);

			log.debug("Q: " + q);

			var docs = searcher.search(q, max);

			log.debug("Results: " + docs.totalHits.value);

			var results = Arrays.stream(docs.scoreDocs).map(hit -> {
				try {
					var doc = searcher.doc(hit.doc);

					String text = null;
					
					if (false == onlyId) {

						text = doc.getField(MySearchDocument.TEXT_ID).stringValue();

						if (hilite) {
							text = getHighlightedField(q, index, MySearchDocument.TEXT_ID, text);
						}

					}

					var id = doc.getField(MySearchDocument.DOC_ID).stringValue();
					var date = doc.getField(MySearchDocument.DATE_ID).stringValue();
					return new MySearchResult(id, hit.score, date, text);
				} catch (Exception e) {
					log.error("Error: ", e);
				}
				return null;
			}).collect(Collectors.toList());

			result.setSize(results.size());
			result.setResults(results);

		} catch (ParseException e) {
			log.error("Error: ", e);
		}
*/
		return result;
	}

	private String getHighlightedField(Query query, SearchIndex index, String fieldName, String fieldValue)
			throws IOException, InvalidTokenOffsetsException {
		var formatter = new SimpleHTMLFormatter("<span class='mysearch-hilite'>", "</span>");
		QueryScorer queryScorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, queryScorer);
		highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, Integer.MAX_VALUE));
		highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
		return highlighter.getBestFragment(index.getAnalyzer(), fieldName, fieldValue);
	}

	@Override
	public String[] getSupportedHttpMethods() {
		return new String[] { HttpMethod.GET.name()};
	}

}

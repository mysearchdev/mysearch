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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.mysearch.model.MySearchDocument;
import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import dev.mysearch.search.IndexService;
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

			var msd = MySearchDocument.from(doc, ctx.getDocumentId(), docs.scoreDocs[0].score);

			return msd;

		}

		throw new MySearchException("Document not found", HttpStatus.NOT_FOUND);
	}

	@Override
	public String[] getSupportedHttpMethods() {
		return new String[] { HttpMethod.GET.name() };
	}

}

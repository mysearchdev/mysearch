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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import dev.mysearch.common.JsonHelper;
import dev.mysearch.model.MySearchDocument;
import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import dev.mysearch.search.IndexService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DocumentAddEndpoint extends AbstractRestEndpoint<Boolean> {

	@Autowired
	private IndexService indexService;

	@Override
	public Boolean service(RestEndpointContext ctx) throws MySearchException, Exception {

		final var text = ctx.getRequestBody();
		try {
			final MySearchDocument obj = JsonHelper.getMapper().readValue(text, MySearchDocument.class);
			indexService.submitToIndexAsync(ctx.getIndexName(), obj);
		} catch (Exception e) {
			log.error("Error: ", e);
			throw new MySearchException("Can't process your JSON request");
		}

		return true;
	}

	@Override
	public String[] getSupportedHttpMethods() {
		return new String[] { HttpMethod.PUT.name()};
	}

}

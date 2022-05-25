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
package dev.mysearch.rest.endpont;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class RestEndpointContext {

	private HttpServletRequest req;

	private String requestBody;

	private String documentId;
	
	private String contentType;

	public String getIndexName() {
		final var path = this.req.getRequestURI();
		final var lastPathSeparator = path.indexOf('/', 1);

		if (lastPathSeparator == -1) {
			return path.substring(1);
		} else {
			return path.substring(1, lastPathSeparator);
		}

	}

	public String getParameter(String name, String defaultValue) {
		var param = req.getParameter(name);
		return StringUtils.isEmpty(param) ? defaultValue : param;
	}

}

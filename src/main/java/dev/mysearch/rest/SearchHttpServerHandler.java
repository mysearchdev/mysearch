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

package dev.mysearch.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.mysearch.rest.action.Urls;
import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import dev.mysearch.rest.endpont.document.DocumentAddEndpoint;
import dev.mysearch.rest.endpont.document.DocumentDeleteByIdEndpoint;
import dev.mysearch.rest.endpont.document.DocumentGetByIdEndpoint;
import dev.mysearch.rest.endpont.document.DocumentsSearchEndpoint;
import dev.mysearch.rest.endpont.index.IndexCreateEndpoint;
import dev.mysearch.rest.endpont.index.IndexDropEndpoint;
import dev.mysearch.rest.endpont.index.IndexGetEndpoint;
import dev.mysearch.rest.endpont.server.ServerInfoEndpoint;
import dev.mysearch.rest.endpont.server.ServerPingEndpoint;
import dev.mysearch.rest.model.RestResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SearchHttpServerHandler implements InitializingBean {

	@Autowired
	private ServerInfoEndpoint serverInfoEndpoint;

	@Autowired
	private ServerPingEndpoint serverPingEndpoint;

	@Autowired
	private IndexCreateEndpoint indexCreateEndpoint;

	@Autowired
	private IndexDropEndpoint indexDropEndpoint;

	@Autowired
	private DocumentAddEndpoint documentAddEndpoint;

	@Autowired
	private DocumentGetByIdEndpoint documentGetByIdEndpoint;

	@Autowired
	private DocumentDeleteByIdEndpoint documentDeleteByIdEndpoint;

	@Autowired
	private DocumentsSearchEndpoint documentsSearchEndpoint;

	@Autowired
	private IndexGetEndpoint indexGetEndpoint;

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@RequestMapping(path = "/**")
	public RestResponse process(HttpServletRequest req, HttpServletResponse resp) {

		final var endpointContext = new RestEndpointContext();
		endpointContext.setReq(req);
		endpointContext.setContentType(req.getHeader(HttpHeaders.CONTENT_TYPE));

		var endpoint = findEnpoint(req, endpointContext);

		if (endpoint == null) {
			return error(resp, "Endpoint not found", HttpStatus.NOT_FOUND);
		}

		if (false == endpoint.isHttpMethodSupported(req.getMethod())) {
			return error(resp,
					"This endpoint support HTTP method " + Arrays.toString(endpoint.getSupportedHttpMethods()),
					HttpStatus.METHOD_NOT_ALLOWED);
		}

		try {
			final var endpointResult = endpoint.service(endpointContext);

			var responseObject = RestResponse.of(endpointResult);

			log.debug("responseObject: " + responseObject);

			return responseObject;

		} catch (MySearchException ex) {

			return error(resp, ex.getMessage(), ex.getStatusCode());

		} catch (Exception e) {

			log.error("Error: ", e);

			return error(resp, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	private final Pattern IndexPattern = Pattern.compile("^/[a-z0-9_]+");

	private final Pattern DocumentPattern = Pattern.compile("^/[a-z0-9_]+/document/(.*)$");

	private final Pattern DocumentSearchPattern = Pattern.compile("^/[a-z0-9_]+/search$");

	private AbstractRestEndpoint findEnpoint(HttpServletRequest req, RestEndpointContext endpointContext) {

		// Extract index name
		final var method = HttpMethod.valueOf(req.getMethod());
		final var rawPath = req.getRequestURI();

		log.debug("Raw path: " + rawPath);

		if (rawPath.equals(Urls.ServerInfo))
			return this.serverInfoEndpoint;

		if (rawPath.equals(Urls.ServerPing))
			return this.serverPingEndpoint;

		// Search documents
		{
			final var matcher = DocumentSearchPattern.matcher(rawPath);
			if (matcher.matches()) {
				return this.documentsSearchEndpoint;
			}
		}

		// Index operations?
		{
			final var matcher = IndexPattern.matcher(rawPath);
			if (matcher.matches()) {
				if (method == HttpMethod.DELETE) {
					return this.indexDropEndpoint;
				} else if (method == HttpMethod.POST) {
					return this.indexCreateEndpoint;
				} else if (method == HttpMethod.GET) {
					return this.indexGetEndpoint;
				}
			}
		}

		// Document add, get by id or delete by id?
		{
			final var matcher = DocumentPattern.matcher(rawPath);
			if (matcher.matches()) {

				endpointContext.setDocumentId(matcher.group(1));

				if (method == HttpMethod.DELETE) {
					return this.documentDeleteByIdEndpoint;
				} else if (method == HttpMethod.GET) {
					return this.documentGetByIdEndpoint;
				} else if (method == HttpMethod.PUT) {

					// get request body
					try {
						var body = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
						endpointContext.setRequestBody(body);
					} catch (IOException e) {
						log.error("Error: ", e);
					}

					return this.documentAddEndpoint;
				}
			}

		}

		return null;

	}

	private RestResponse<Boolean> error(HttpServletResponse resp, String message, HttpStatus status) {
		resp.setStatus(status.value());
		final var error = new RestResponse<Boolean>();
		error.setError(true);
		error.setErrorMessage(message);
		return error;
	}

}
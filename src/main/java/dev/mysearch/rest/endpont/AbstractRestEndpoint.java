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

import org.apache.commons.lang3.ArrayUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRestEndpoint<T> {

	public abstract T service(RestEndpointContext ctx) throws MySearchException, Exception;

	public boolean isHttpMethodSupported(String httpMethod) {
		return ArrayUtils.contains(getSupportedHttpMethods(), httpMethod);
	}

	public abstract String[] getSupportedHttpMethods();

}

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

package dev.mysearch.common;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonHelper {

	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.getSerializerProvider().setNullKeySerializer(new JsonJacksonNullKeySerializer());
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	public static byte[] writeValueAsBytes(Object o) {
		try {
			return mapper.writeValueAsBytes(o);
		} catch (Exception e) {
			log.error("Error: ", e);
		}
		return null;
	}

	public static ObjectMapper getMapper() {
		return mapper;
	}

}

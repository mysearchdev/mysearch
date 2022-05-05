package dev.mysearch.common;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Json {

	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.getSerializerProvider().setNullKeySerializer(new JacksonNullKeySerializer());
		mapper.setSerializationInclusion(Include.NON_NULL);

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

package dev.mysearch.rest.endpont.http;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SearchHttpRequest {

	private String uri;

	private String path;

	private String rawPath;

	private String method;

	private String query;

	private Map<String, List<String>> parameters;

	private List<Map.Entry<String, String>> headers;

}

package dev.mysearch.rest.endpont.http;

import java.io.OutputStream;
import java.io.StringWriter;

import lombok.Data;

@Data
public class SearchHttpResponse {

	private OutputStream os;

	public OutputStream getOutputStream() {
		return null;
	}

	public StringWriter getWriter() {
		return null;
	}

}

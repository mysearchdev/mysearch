package dev.mysearch.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

import dev.mysearch.common.Json;
import lombok.Data;

@Data
public class MySearchDocument {

	public static final String DOC_ID = "___id";
	public static final String TEXT_ID = "___text";
	public static final String DATE_ID = "___date";

	private String id;

	private Float score;

	private Set<Property> attributes = new HashSet<>();

	public MySearchDocument() {
		super();
	}

	public MySearchDocument(String id) {
		super();
		this.id = id;
	}
	
	public static void main(String[] args) throws JsonProcessingException {
		var o = new MySearchDocument();
		o.setId(UUID.randomUUID().toString());
		o.getAttributes().add(new Property("text", "25 Aug 2017 — Microsoft SQL Server comes up with an answer to part of this issue with a Full-Text Search feature. This feature lets users and application ...\n", DataType.Text));
		o.getAttributes().add(new Property("dob", "", DataType.Text));
		System.out.println(Json.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o));
	}

}

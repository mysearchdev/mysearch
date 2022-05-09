package dev.mysearch.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class MySearchDocument {

	public static final String DOC_ID = "___id";
	public static final String TEXT_ID = "___text";
	public static final String DATE_ID = "___date";

	private String id;

	private Float score;

	private Set<Attribute> attributes = new HashSet<>();

	public MySearchDocument() {
		super();
	}

	public MySearchDocument(String id) {
		super();
		this.id = id;
	}

}

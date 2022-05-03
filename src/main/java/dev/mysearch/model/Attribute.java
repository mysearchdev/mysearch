package dev.mysearch.model;

import lombok.Data;

@Data
public class Attribute {
	
	private String name;
	
	private String value;
	
	private DataType type;
	
	private boolean indexed;
	
	private boolean stored;
	
}

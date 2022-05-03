package dev.mysearch.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Document {

	private List<Attribute> attributes = new ArrayList<>();
	
}

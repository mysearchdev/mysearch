package dev.mysearch.model;

import java.util.Objects;

import lombok.Data;

@Data
public class Attribute {

	private String name;

	private String value;

	private DataType type;

	public static Attribute of(String name, String value) {
		return of(name, value, null);
	}

	public static Attribute of(String name, String value, DataType type) {
		var attr = new Attribute();
		attr.name = name;
		attr.value = value;
		attr.type = type;
		return attr;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attribute other = (Attribute) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}

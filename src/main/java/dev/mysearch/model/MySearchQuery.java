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
package dev.mysearch.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class MySearchQuery {

	public static enum Occur {
		Must, Should, MustNot
	}

	public static enum Operator {
		Eq, Leq, Geq, Ge, Le,
	}

	private int max = 100;

	private Boolean idsOnly = false;

	@Data
	@AllArgsConstructor
	@JsonInclude(Include.NON_NULL)
	public static class Field {
		private String query;
		private String field;
		private Float boost;
		private Occur occur;
	}

	private Set<Field> queries = new HashSet<>();

	@Data
	static class Q {
		private List<String> columns = new ArrayList<>();
		private String index;
		private List<Clause> clauses = new ArrayList<>();
		@Override
		public String toString() {
			var sb=new StringBuilder();
			sb.append(System.lineSeparator());
			sb.append("\tQ index: " + index );
			sb.append(System.lineSeparator());
			sb.append("\tQ columns: " + columns );
			sb.append(System.lineSeparator());
			sb.append("\tQ clauses: ");
			sb.append(System.lineSeparator());
			clauses.forEach(c->{
				sb.append("\t\t"+c);
				sb.append(System.lineSeparator());	
			});
			return sb.toString();
		}
		
		
	}

	@Data
	public static class Clause {
		private Clause parent;
		private String left;
		private String operator;
		private List<String> right = new ArrayList<>();
	}

}

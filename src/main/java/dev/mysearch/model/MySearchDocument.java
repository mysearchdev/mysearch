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

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;

import dev.mysearch.common.JsonHelper;
import dev.mysearch.model.MySearchDocument.Property.DataType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@JsonInclude(Include.NON_NULL)
public class MySearchDocument {

	public static final String DOC_ID = "___id";
	public static final String DATE_ID = "___date";

	private String id;

	private Float score;

	private Set<Property> properties = new HashSet<>();

	@Data
	@JsonInclude(Include.NON_NULL)
	public static class Property {

		public static enum DataType {
			text, string, number, date,
		}

		private String name;

		private Object value;

		private DataType type;

		public Property() {
			super();
		}

		public Property(String name, Object value, DataType type) {
			super();
			this.name = name;
			this.value = value;
			this.type = type;
		}

	}

	public MySearchDocument() {
		super();
	}

	public MySearchDocument(String id) {
		super();
		this.id = id;
	}

	public Document toLuceneDocument() {

		var doc = new Document();

		var sb = new StringBuilder();
		
		this.properties.forEach(attr -> {

			doc.add(new StringField(DOC_ID, this.getId(), Field.Store.YES));

			Field f = null;

			if (attr.getType() == DataType.string) {

				f = new StringField(attr.getName(), attr.getValue().toString(), Field.Store.YES);

			} else if (attr.getType() == DataType.text) {

				f = new TextField(attr.getName(), attr.getValue().toString(), Field.Store.YES);
				
				sb.append(attr.getValue().toString());
				sb.append(" ");

			} else if (attr.getType() == DataType.number) {

//				doc.add(new DoublePoint(attr.getName(), Double.valueOf(attr.getValue().toString())));

//				if (attr.stored) {
//					f = new StoredField(attr.getName(), attr.getValue().toString());
//				}

				f = new DoublePoint(attr.getName(), Double.valueOf(attr.getValue().toString()));

			} else if (attr.getType() == DataType.date) {

				try {
					Date date = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT
							.parse(attr.getValue().toString());
					f = new LongPoint(attr.getName(), date.getTime());

				} catch (ParseException e) {
					log.error("Error: ", e);
				}

			}
			
			doc.add(f);

		});
		
		doc.add(new StringField(DATE_ID, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date()), Field.Store.YES));

		return doc;
	}

	public static MySearchDocument from(Document doc, String id, Float score) {

		MySearchDocument m = new MySearchDocument(id);
		m.setScore(score);

		doc.getFields().forEach(f -> {
			if (false == f.name().equals(MySearchDocument.DOC_ID)) {
				m.getProperties().add(new MySearchDocument.Property(f.name(), f.stringValue(), null));
			}
		});

		return m;
	}


	public static void main(String[] args) throws JsonProcessingException {
		var o = new MySearchDocument();
		o.setId("1");
		
		o.getProperties().add(new Property("title",
				"HubbleDotNet is an free open-source full-text search database ",
				DataType.text));
		
		o.getProperties().add(new Property("text",
				"25 Aug 2017 — Microsoft SQL Server comes up with an answer to part of this issue with a Full-Text Search feature. This feature lets users and application ...\n",
				DataType.text));
		
		o.getProperties().add(new Property("summary",
				"		o.getProperties().add(new Property(\"text\",\n"
				+ "				\"25 Aug 2017 — Microsoft SQL Server comes up with an answer to part of this issue with a Full-Text Search feature. This feature lets users and application ...\\n\",\n"
				+ "				DataType.text));\n"
				+ "",
				DataType.text));
		
		o.getProperties().add(new Property("dob",
				DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date()), DataType.date));
		o.getProperties().add(new Property("age", 24, DataType.number));
		o.getProperties().add(new Property("height", 1.84, DataType.number));
		o.getProperties().add(new Property("uuid", UUID.randomUUID().toString(), DataType.string));
		System.out.println(JsonHelper.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o));

		// select * from test where name='name' and height > 20
	}

}

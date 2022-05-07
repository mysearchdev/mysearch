package dev.mysearch.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class MySearchDocument {

	public static final String DOC_ID = "___id";

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

	public static void main(String[] args) throws JsonProcessingException {
		MySearchDocument doc = new MySearchDocument("1");
		doc.getAttributes().add(Attribute.of("name", "Serg", DataType.Token));
		doc.getAttributes().add(Attribute.of("about",
				"The -docs command-line parameter value is the location of the directory containing files to be indexed.",
				DataType.Text));

		var mapper = new ObjectMapper();

		var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc);

		System.out.println(json);
	}

	public Iterable<? extends IndexableField> toLuceneDocument() {
		var doc = new Document();
		this.attributes.forEach(attr -> {
			doc.add(new StringField(DOC_ID, this.getId(), Field.Store.YES));
			Field f = null;
			if (attr.getType() == DataType.Token) {
				f = new StringField(attr.getName(), attr.getValue(), Field.Store.YES);
			} else if (attr.getType() == DataType.Text) {
				f = new TextField(attr.getName(), attr.getValue(), Field.Store.YES);
			} else if (attr.getType() == DataType.Integer) {
				var pt = new IntPoint(attr.getName(), Integer.valueOf(attr.getValue()));
				doc.add(pt);
				f = new StoredField(attr.getName(), attr.getValue());
			} else if (attr.getType() == DataType.Long) {
				var pt = new LongPoint(attr.getName(), Long.valueOf(attr.getValue()));
				doc.add(pt);
				f = new StoredField(attr.getName(), attr.getValue());
			} else if (attr.getType() == DataType.Float) {
				var pt = new FloatPoint(attr.getName(), Float.valueOf(attr.getValue()));
				doc.add(pt);
				f = new StoredField(attr.getName(), attr.getValue());
			} else if (attr.getType() == DataType.Double) {
				var pt = new DoublePoint(attr.getName(), Double.valueOf(attr.getValue()));
				doc.add(pt);
				f = new StoredField(attr.getName(), attr.getValue());
			}
			doc.add(f);
		});
		return doc;
	}

	public static MySearchDocument from(Document doc, String id, Float score) {

		MySearchDocument m = new MySearchDocument(id);
		m.setScore(score);

		doc.getFields().forEach(f -> {
			if (false == f.name().equals(MySearchDocument.DOC_ID)) {
				m.getAttributes().add(Attribute.of(f.name(), f.stringValue()));
			}
		});

		return m;
	}

}

package dev.mysearch.search;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;

import dev.mysearch.model.MySearchDocument;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IndexContext {

	private StandardAnalyzer analyzer;
	private IndexWriterConfig iwc;
	private Path indexDir;
	private FSDirectory dir;
	private IndexWriter indexWriter;
	private DirectoryReader reader;

	private boolean readerNeedsToReopen;

	public IndexContext(String rootIndexDir, String indexName, OpenMode openMode) throws IOException {

		indexDir = Path.of(rootIndexDir, indexName);

		dir = FSDirectory.open(indexDir);
		analyzer = new StandardAnalyzer();
		iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(openMode);
		indexWriter = new IndexWriter(dir, iwc);
	}

	public IndexSearcher getSearcher() {
		return new IndexSearcher(getReader());
	}

	public DirectoryReader getReader() {

		try {

			if (readerNeedsToReopen) {
				readerNeedsToReopen = false;
				if (reader != null)
					IOUtils.close(reader);
				reader = null;
			}

			if (reader == null) {
				reader = DirectoryReader.open(dir);
			}
		} catch (IOException e) {
			log.error("Error: ", e);
		}
		return reader;
	}

	public void close() {
		log.debug("Closed index: " + dir);
		IOUtils.closeQuietly(reader);
		IOUtils.closeQuietly(indexWriter);
		IOUtils.closeQuietly(analyzer);
		IOUtils.closeQuietly(dir);
	}

	public void updateDocument(Term term, Iterable<? extends IndexableField> luceneDocument) throws IOException {
		this.indexWriter.updateDocument(term, luceneDocument);
		readerNeedsToReopen = true;
	}

	public void commit() throws IOException {
		this.indexWriter.commit();
		readerNeedsToReopen = true;
	}

	public Path getIndexDir() {
		return this.indexDir;
	}

	public void deleteById(String id) throws IOException {
		var term = new Term(MySearchDocument.DOC_ID, id);
		this.indexWriter.deleteDocuments(term);
		commit();
	}

}

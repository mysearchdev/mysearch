package dev.mysearch.search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import dev.mysearch.model.MySearchDocument;
import dev.mysearch.rest.endpont.MySearchException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchIndex {

	private Analyzer analyzer;
	private IndexWriterConfig iwc;
	private File indexDir;
	private FSDirectory dir;
	private IndexWriter indexWriter;
	private DirectoryReader reader;
	private Properties properties;

	private boolean readerNeedsToReopen;

	public SearchIndex(String rootIndexDir, String indexName, OpenMode openMode) throws MySearchException {

		indexDir = new File(rootIndexDir, indexName);

		try {

			dir = FSDirectory.open(indexDir.toPath());

			analyzer = new EnglishAnalyzer();

			iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(openMode);
			indexWriter = new IndexWriter(dir, iwc);

			// load .meta properties file
			var meta = new File(dir.getDirectory().toFile(), ".meta");
			if (meta.exists()) {
				try (var fr = new FileReader(meta)) {
					this.properties = new Properties();
					this.properties.load(fr);
					log.debug("Load index .meta: " + this.properties);
				}
			}
			
			if (openMode == OpenMode.CREATE) {
				indexWriter.commit();
			}

		} catch (IndexNotFoundException e) {
			FileUtils.deleteQuietly(indexDir);
			throw new MySearchException("Index '" + indexName + "' does not exist");
		} catch (IOException e) {
			FileUtils.deleteQuietly(indexDir);
			throw new MySearchException("I/O error opening an index");
		}
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

	public File getIndexDir() {
		return this.indexDir;
	}

	public void deleteById(String id) throws IOException {
		var term = new Term(MySearchDocument.DOC_ID, id);
		this.indexWriter.deleteDocuments(term);
		commit();
	}

	public Analyzer getAnalyzer() {
		return this.analyzer;
	}

	public void setProperties(Properties props) {
		this.properties = props;
	}

	public Properties getProperties() {
		return properties;
	}

}

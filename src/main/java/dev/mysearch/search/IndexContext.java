package dev.mysearch.search;

import java.nio.file.Path;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class IndexContext {

	private StandardAnalyzer analyzer;
	private IndexWriterConfig iwc;
	private Path indexDir;
	private FSDirectory dir;

	public IndexContext(String rootIndexDir, String indexName) throws Exception {

		indexDir = Path.of(rootIndexDir, indexName);

		dir = FSDirectory.open(indexDir);
		analyzer = new StandardAnalyzer();
		iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

	}

	public void close() throws Exception {
		analyzer.close();
		dir.close();
	}

}

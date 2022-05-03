package dev.mysearch.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.mysearch.model.Document;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IndexService implements InitializingBean, DisposableBean {

	@Value("${mysearch.index.location}")
	private String rootIndexDirectory;

	private Map<String, IndexContext> indexContexts = new HashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {

		log.info("Root index dir: " + rootIndexDirectory);

		// Is root folder readable and writable?
		var dir = new File(rootIndexDirectory);

		if (false == dir.exists()) {
			FileUtils.forceMkdir(dir);
		}

		if (false == dir.canRead() || false == dir.canWrite()) {
			log.error("The dir is not readable or writable: " + dir);
		}
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

	public void createIndex(String indexName) throws Exception {

		// exists?
		var indexDir = new File(rootIndexDirectory, indexName);

		if (indexDir.exists()) {
			throw new Exception("Index '" + indexName + "' already exists");
		}

		if (false == indexContexts.containsKey(indexName)) {

			log.info("Creating index " + indexName);

			var indexContext = new IndexContext(rootIndexDirectory, indexName);

			this.indexContexts.put(indexName, indexContext);

			// write meta file
			var metafile = Path.of(indexContext.getIndexDir().toAbsolutePath().toString(), ".meta");
			Files.writeString(metafile, DateFormatUtils.SMTP_DATETIME_FORMAT.format(new Date()));

		}

	}

	public void dropIndex(String indexName) throws Exception {

		// exists?
		var indexDir = new File(rootIndexDirectory, indexName);

		if (false == indexDir.exists()) {
			throw new Exception("Index '" + indexName + "' does not exist");
		}

		try {
			FileUtils.forceDelete(indexDir);
		} catch (IOException e) {
			throw new Exception("Index '" + indexName + "' was not dropped: " + e.getMessage());
		}

		if (indexContexts.containsKey(indexName)) {
			var ctx = indexContexts.get(indexName);
			ctx.close();
			indexContexts.remove(indexName);
		}
		
		log.debug("Index '" + indexName + "' was dropped");

	}

	public void add(Document doc, String index) {

	}

}

package dev.mysearch.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.mysearch.model.MySearchDocument;
import dev.mysearch.rest.endpont.MySearchException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IndexService implements InitializingBean, DisposableBean {

	@Value("${mysearch.index.location}")
	private String rootIndexDirectory;

	private Map<String, SearchIndex> indexContexts = new HashMap<>();

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
			System.exit(1);
		}
	}

	@Override
	public void destroy() throws Exception {
		indexContexts.values().forEach(ctx -> {
			try {
				ctx.close();
			} catch (Exception e) {
				log.error("Error: ", e);
			}
		});
	}
	
	public SearchIndex getExistingIndex(String indexName) throws Exception {
		
		if (this.indexContexts.containsKey(indexName)) {
			return this.indexContexts.get(indexName);
		}

		// exists?
		var indexDir = new File(rootIndexDirectory, indexName);

		if (false == indexDir.exists()) {
			throw new MySearchException("Index '" + indexName + "' does not exist");
		}

		var indexContext = getIndexContext(indexName, OpenMode.APPEND);
		
		this.indexContexts.put(indexName, indexContext);
		
		return indexContext;

	}
	

	public void createIndex(String indexName) throws Exception {

		// exists?
		var indexDir = new File(rootIndexDirectory, indexName);

		if (indexDir.exists()) {
			throw new MySearchException("Index '" + indexName + "' already exists");
		}

		var indexContext = getIndexContext(indexName, OpenMode.CREATE);

		// write meta file
		var metafile = Path.of(indexContext.getIndexDir().getAbsolutePath().toString(), ".meta");
		Files.writeString(metafile, DateFormatUtils.SMTP_DATETIME_FORMAT.format(new Date()));

	}

	public SearchIndex getIndexContext(String indexName, OpenMode openMode) throws IOException, MySearchException {

		var ctx = indexContexts.get(indexName);

		if (ctx == null) {
			log.info("Get index " + indexName);
			ctx = new SearchIndex(rootIndexDirectory, indexName, openMode);
			this.indexContexts.put(indexName, ctx);
		}

		return ctx;
	}

	public void dropIndex(String indexName) throws MySearchException, IOException {

		// exists?
		var indexDir = new File(rootIndexDirectory, indexName);

		if (false == indexDir.exists()) {
			throw new MySearchException("Index '" + indexName + "' does not exist");
		}

		if (indexContexts.containsKey(indexName)) {
			indexContexts.get(indexName).close();
			indexContexts.remove(indexName);
		}

		try {
			FileUtils.forceDelete(indexDir);
		} catch (IOException e) {
			throw new MySearchException("Index '" + indexName + "' was not dropped: " + e.getMessage());
		}

		log.debug("Index '" + indexName + "' was dropped");

	}

	public void add(MySearchDocument doc, String index) {

	}

}

package dev.mysearch.search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.mysearch.model.MySearchDocument;
import dev.mysearch.rest.endpont.MySearchException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IndexService implements InitializingBean, DisposableBean {

	@Value("${mysearch.index.location}")
	private String rootIndexDirectory;

	@Data
	public static class IndexInfo {
		private String index;
		private int documents;
		private String directory;
		private Properties properties;
		private long freeSpace;
		private long totalSpace;
		private long usableSpace;
	}

	private Map<String, SearchIndex> indexContexts = new HashMap<>();

	public void setRootIndexDirectory(String rootIndexDirectory) {
		this.rootIndexDirectory = rootIndexDirectory;
	}

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

		var indexContext = getIndexContext(indexName, OpenMode.APPEND, Lang.en);

		this.indexContexts.put(indexName, indexContext);

		return indexContext;

	}

	public boolean doesIndexExists(String indexName) {

		// Dir exists?
		var indexDir = new File(rootIndexDirectory, indexName);

		// If no Dir, obviously no index
		if (false == indexDir.exists()) {
			log.debug("No index dir at " + indexDir);
			return false;
		}

		// If Dir is there, check if the index actually exists and can be opened
		
		if (this.indexContexts.containsKey(indexName)) {
			return true;
		}
		
		try {
			var ctx = new SearchIndex(rootIndexDirectory, indexName, OpenMode.APPEND, Lang.en);
			ctx.close();
		} catch (Exception e) {
			log.debug("Can't open index at " + indexDir);
			return false;
		}

		return true;

	}

	public SearchIndex createNewIndex(String indexName, Lang lang) throws Exception {

		// exists?
		var indexDir = new File(rootIndexDirectory, indexName);

		if (indexDir.exists()) {
			throw new MySearchException("Index '" + indexName + "' already exists");
		}

		var indexContext = getIndexContext(indexName, OpenMode.CREATE, lang);

		// write meta file
		var props = new Properties();
		props.put("time", String.valueOf(System.currentTimeMillis()));
		props.put("locale", Locale.getDefault().getDisplayName());
		props.put("lang", lang.toString());

		try (var writer = new FileWriter(new File(indexContext.getIndexDir().getAbsolutePath().toString(), ".meta"))) {
			props.store(writer, "");
		}
		indexContext.setProperties(props);
		
		return indexContext;

	}

	private SearchIndex getIndexContext(String indexName, OpenMode openMode, Lang lang) throws IOException, MySearchException {

		var ctx = indexContexts.get(indexName);

		if (ctx == null) {
			log.info("Get index " + indexName);
			ctx = new SearchIndex(rootIndexDirectory, indexName, openMode, lang);
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

	public IndexInfo getIndexInfo(String indexName) throws Exception {

		// exists?
		var indexDir = new File(rootIndexDirectory, indexName);

		if (false == indexDir.exists()) {
			throw new MySearchException("Index '" + indexName + "' does not exist");
		}

		var index = this.getExistingIndex(indexName);

		var info = new IndexInfo();
		info.setIndex(indexName);
		info.setProperties(index.getProperties());
		info.setDirectory(index.getIndexDir().getAbsolutePath());
		info.setFreeSpace(index.getIndexDir().getFreeSpace());
		info.setTotalSpace(index.getIndexDir().getTotalSpace());
		info.setUsableSpace(index.getIndexDir().getUsableSpace());
		info.setDocuments(index.getReader().numDocs());

		return info;

	}

}

package dev.mysearch.search;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestIndexService {

	IndexService indexService;
	File root = new File(SystemUtils.getJavaIoTmpDir(), UUID.randomUUID().toString());

	@Before
	public void init() throws Exception {
		
		this.indexService = new IndexService();
		this.indexService.setRootIndexDirectory(root.getAbsolutePath());
		
		FileUtils.forceMkdir(root);
		
	}

	@Test
	public void checkNoIndexAtNonExistingFolder() {
		var test = indexService.doesIndexExists("no_folder");
		assertFalse(test);
	}

	@Test
	public void checkNoIndexAtExistingFolder() throws IOException {
		var folder = new File(root, "folder");
		FileUtils.forceMkdir(folder);
		var test = indexService.doesIndexExists("folder");
		FileUtils.deleteDirectory(folder);
		assertFalse(test);
	}
	
	@After
	public void close() throws Exception {
		FileUtils.deleteDirectory(root);
	}

}

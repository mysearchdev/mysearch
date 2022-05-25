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
package dev.mysearch.search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestIndexService {

	static IndexService indexService;
	static File root = new File(SystemUtils.getJavaIoTmpDir(), UUID.randomUUID().toString());

	@BeforeAll
	public static void init() throws Exception {

		indexService = new IndexService();
		indexService.setRootIndexDirectory(root.getAbsolutePath());

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

	@Test
	public void checkIndexCreated() throws Exception {
		var index = indexService.createNewIndex("test-index", Lang.en);
		assertNotNull(index);
		assertNotNull(index.getIndexDir());
		assertTrue(index.getIndexDir().exists());
		assertTrue(indexService.doesIndexExists("test-index"));
	}

	@AfterAll
	public static void close() throws Exception {
		FileUtils.deleteDirectory(root);
	}

}

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

package dev.mysearch;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Server {

	/*
	 * 
	 * Precedence of options:
	 * 
	 * If you specify an option by using one of the environment variables described
	 * in this topic, it overrides any value loaded from a profile in the
	 * configuration file.
	 * 
	 * If you specify an option by using a parameter on the CLI command line, it
	 * overrides any value from either the corresponding environment variable or a
	 * profile in the configuration file.
	 * 
	 * log file location: path display banner?: boolean shutdown: port
	 * 
	 * TODO: generate user/password on 1st start, allow reset/regeneration, keep
	 * audits allow open access - with WARN messages
	 * 
	 */
	public static void main(String[] args) throws Exception {
		
		if (args != null)
			for (var arg : args) {
				log.info("Arg: [" + arg + "]");
			}

		log.info("Working dir: " + new File("").getAbsolutePath());

		log.info(ManagementFactory.getRuntimeMXBean().getClassPath());
		log.info(ManagementFactory.getRuntimeMXBean().getLibraryPath());
		log.info(ManagementFactory.getRuntimeMXBean().getManagementSpecVersion());
		log.info(ManagementFactory.getRuntimeMXBean().getName());
		log.info(ManagementFactory.getRuntimeMXBean().getSpecName());
		log.info(ManagementFactory.getRuntimeMXBean().getSpecVendor());
		log.info(ManagementFactory.getRuntimeMXBean().getSpecVersion());
		log.info(ManagementFactory.getRuntimeMXBean().getVmName());
		log.info(ManagementFactory.getRuntimeMXBean().getVmVendor());
		log.info(ManagementFactory.getRuntimeMXBean().getVmVersion()+"");
		log.info(ManagementFactory.getRuntimeMXBean().getInputArguments()+"");
		log.info(ManagementFactory.getRuntimeMXBean().getSystemProperties()+"");

	    SpringApplication.run(Server.class, args);

	}
	

}

package dev.mysearch;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Server {

	public static ClassPathXmlApplicationContext ctx;

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

		System.out.println("PID: " + ProcessHandle.current().pid());

		if (args != null)
			for (var arg : args) {
				System.out.println("Arg: [" + arg + "]");
			}

		System.out.println(IOUtils.toString(Server.class.getResourceAsStream("/banner.txt"), StandardCharsets.UTF_8));

		System.out.println("Working dir: " + new File("").getAbsolutePath());

		System.out.println(ManagementFactory.getRuntimeMXBean().getClassPath());
		System.out.println(ManagementFactory.getRuntimeMXBean().getLibraryPath());
		System.out.println(ManagementFactory.getRuntimeMXBean().getManagementSpecVersion());
		System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		System.out.println(ManagementFactory.getRuntimeMXBean().getSpecName());
		System.out.println(ManagementFactory.getRuntimeMXBean().getSpecVendor());
		System.out.println(ManagementFactory.getRuntimeMXBean().getSpecVersion());
		System.out.println(ManagementFactory.getRuntimeMXBean().getVmName());
		System.out.println(ManagementFactory.getRuntimeMXBean().getVmVendor());
		System.out.println(ManagementFactory.getRuntimeMXBean().getVmVersion());
		System.out.println(ManagementFactory.getRuntimeMXBean().getInputArguments());
		System.out.println(ManagementFactory.getRuntimeMXBean().getSystemProperties());

		ctx = new ClassPathXmlApplicationContext("/spring-context.xml");
		ctx.start();

	}

	public void close() {
		ctx.stop();
		ctx.close();
	}

}
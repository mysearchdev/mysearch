package dev.mysearch.performance;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PopulateFrankenstein {

	public static void main(String[] args) throws Exception {

		List<String> texts = new ArrayList<>();

		var sb = new StringBuilder();

		FileUtils.readLines(new File("/home/sergio/texts/84-0.txt"), StandardCharsets.UTF_8).stream().forEach(l -> {
			if (l.strip().isEmpty()) {
				texts.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(l);
				sb.append(IOUtils.LINE_SEPARATOR);
			}
		});

		log.debug("Texts: " + texts.size());

		var httpClient = HttpClient.newBuilder().build();

		int index = 1;

		var ex = Executors.newFixedThreadPool(4);

		for (var p : texts) {

			final var request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/test/document/" + index))
					.headers("Content-Type", "text/plain;charset=UTF-8").PUT(HttpRequest.BodyPublishers.ofString(p))
					.build();

			ex.submit(() -> {
				try {

					var st = System.currentTimeMillis();

					httpClient.send(request, HttpResponse.BodyHandlers.ofString());

					var et = System.currentTimeMillis();

					log.debug("Sent: " + (et - st) + " ms.");

				} catch (Exception e) {
					log.error("Error: ", e);
				}
			});

			index++;
		}

		ex.shutdown();

		Thread.currentThread().sleep(10000);

	}

}

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PopulateCobra1 {

	public static void main(String[] args) throws Exception {

		final var indexName = "ru";

		List<String> texts = new ArrayList<>();

		var sb = new StringBuilder();

		FileUtils.readLines(new File("/home/sergio/texts/ru-1.txt"), StandardCharsets.UTF_8).stream().forEach(l -> {
			if (l.strip().isEmpty()) {
				texts.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(l);
				sb.append(IOUtils.LINE_SEPARATOR);
			}
		});

		log.debug("Texts: " + texts.size());

		var httpClient = HttpClient.newHttpClient();

		int index = 1;
		for (var p : texts) {

			var st = System.currentTimeMillis();

			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI("http://localhost:8080/" + indexName + "/document/" + index))
					.headers("Content-Type", "text/plain;charset=UTF-8").PUT(HttpRequest.BodyPublishers.ofString(p))
					.build();

			httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			var et = System.currentTimeMillis();

			log.debug("Sent: " + (et - st) + " ms.");

			index++;
		}

	}

}

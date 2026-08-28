package edu.itba.dps.tp1.exchange.infrastructure.http;

import com.sun.net.httpserver.HttpServer;
import edu.itba.dps.tp1.exchange.io.http.HttpConnectionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnirestHttpClientTest {

	private final UnirestHttpClient client = new UnirestHttpClient();
	private HttpServer server;

	@AfterEach
	void tearDown() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	void returnsTheBodyAndStatusCodeOfARealResponse() throws IOException {
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/ping", exchange -> {
			final byte[] body = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, body.length);
			exchange.getResponseBody().write(body);
			exchange.close();
		});
		server.start();

		final var response = client.get(
				URI.create("http://localhost:" + server.getAddress().getPort() + "/ping"),
				Map.of(), Map.of());

		assertEquals(200, response.statusCode());
		assertEquals("{\"ok\":true}", response.body());
	}

	@Test
	void wrapsUnreachableHostsInAConnectionException() {
		assertThrows(HttpConnectionException.class,
				() -> client.get(URI.create("http://localhost:1/unreachable"), Map.of(), Map.of()));
	}
}

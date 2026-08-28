package edu.itba.dps.tp1.exchange.infrastructure.api;

import edu.itba.dps.tp1.exchange.domain.exception.CurrencyApiException;
import edu.itba.dps.tp1.exchange.domain.exception.CurrencyConnectionException;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.io.http.HttpConnectionException;
import edu.itba.dps.tp1.exchange.io.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyApiGatewayTest {

	@Mock
	private HttpClient httpClient;

	private CurrencyApiGateway gateway;

	@BeforeEach
	void setUp() {
		gateway = new CurrencyApiGateway(httpClient, "the-api-key");
	}

	@Test
	void returnsTheBodyOnASuccessfulResponse() {
		when(httpClient.get(eq(URI.create("https://api.currencyapi.com/v3/latest")), any(), any()))
				.thenReturn(new HttpResponse("{\"ok\":true}", 200));

		assertEquals("{\"ok\":true}", gateway.get("latest", Map.of()));
	}

	@Test
	void sendsTheApiKeyAsAHeader() {
		when(httpClient.get(any(), any(), eq(Map.of("accept", "application/json", "apikey", "the-api-key"))))
				.thenReturn(new HttpResponse("{}", 200));

		gateway.get("latest", Map.of());
	}

	@Test
	void translatesNonSuccessStatusesIntoACurrencyApiException() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("not found", 404));

		final var exception = assertThrows(CurrencyApiException.class, () -> gateway.get("latest", Map.of()));

		assertEquals(404, exception.statusCode());
	}

	@Test
	void translatesTransportFailuresIntoACurrencyConnectionException() {
		final var cause = new HttpConnectionException("boom", new RuntimeException());
		when(httpClient.get(any(), any(), any())).thenThrow(cause);

		final var exception = assertThrows(CurrencyConnectionException.class, () -> gateway.get("latest", Map.of()));

		assertEquals(cause, exception.getCause());
	}
}

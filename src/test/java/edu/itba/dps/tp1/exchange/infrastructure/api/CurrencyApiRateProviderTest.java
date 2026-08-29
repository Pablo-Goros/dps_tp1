package edu.itba.dps.tp1.exchange.infrastructure.api;

import edu.itba.dps.tp1.exchange.domain.exception.CurrencyNotAvailableException;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.io.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyApiRateProviderTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency BRL = Currency.getInstance("BRL");
	private static final Currency EUR = Currency.getInstance("EUR");

	// Matches the documented FreeCurrencyAPI /v1/latest response shape.
	private static final String RESPONSE_BODY = """
			{"data":{"BRL":5.4321,"EUR":0.8581081196}}""";

	@Mock
	private HttpClient httpClient;

	private CurrencyApiRateProvider provider;

	@BeforeEach
	void setUp() {
		provider = new CurrencyApiRateProvider(httpClient, "the-api-key");
	}

	@Test
	void requestsTheLatestEndpointWithTheBaseAndTargetCurrencies() {
		when(httpClient.get(eq(URI.create("https://api.freecurrencyapi.com/v1/latest")),
				eq(Map.of("base_currency", "USD", "currencies", "BRL,EUR")), any()))
				.thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		provider.getRates(USD, List.of(BRL, EUR));
	}

	@Test
	void parsesEachRequestedCurrencyWithTheSharedRetrievalTimestamp() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		final var beforeRequest = java.time.Instant.now();
		final var rates = provider.getRates(USD, List.of(BRL, EUR));
		final var afterRequest = java.time.Instant.now();

		final var timestamp = rates.get(BRL).timestamp();
		assertEquals(5.4321, rates.get(BRL).rate());
		assertEquals(0.8581081196, rates.get(EUR).rate());
		assertEquals(timestamp, rates.get(EUR).timestamp());
		assertFalse(timestamp.isBefore(beforeRequest));
		assertFalse(timestamp.isAfter(afterRequest));
	}

	@Test
	void failsWhenTheResponseIsMissingARequestedCurrency() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		final var jpy = Currency.getInstance("JPY");

		assertThrows(CurrencyNotAvailableException.class, () -> provider.getRates(USD, List.of(jpy)));
	}
}

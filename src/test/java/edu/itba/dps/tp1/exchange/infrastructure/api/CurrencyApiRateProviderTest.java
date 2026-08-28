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
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyApiRateProviderTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency ARS = Currency.getInstance("ARS");
	private static final Currency EUR = Currency.getInstance("EUR");

	// Captured from a real call to api.currencyapi.com/v3/latest.
	private static final String RESPONSE_BODY = """
			{"meta":{"last_updated_at":"2026-08-27T23:59:59Z"},"data":{"ARS":{"code":"ARS","value":1512.1302977769},"EUR":{"code":"EUR","value":0.8581081196}}}""";

	@Mock
	private HttpClient httpClient;

	private CurrencyApiRateProvider provider;

	@BeforeEach
	void setUp() {
		provider = new CurrencyApiRateProvider(httpClient, "the-api-key");
	}

	@Test
	void requestsTheLatestEndpointWithTheBaseAndTargetCurrencies() {
		when(httpClient.get(eq(URI.create("https://api.currencyapi.com/v3/latest")),
				eq(Map.of("base_currency", "USD", "currencies", "ARS,EUR")), any()))
				.thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		provider.getRates(USD, List.of(ARS, EUR));
	}

	@Test
	void parsesEachRequestedCurrencyWithTheSharedTimestamp() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		final var rates = provider.getRates(USD, List.of(ARS, EUR));

		final var timestamp = Instant.parse("2026-08-27T23:59:59Z");
		assertEquals(1512.1302977769, rates.get(ARS).rate());
		assertEquals(timestamp, rates.get(ARS).timestamp());
		assertEquals(0.8581081196, rates.get(EUR).rate());
		assertEquals(timestamp, rates.get(EUR).timestamp());
	}

	@Test
	void failsWhenTheResponseIsMissingARequestedCurrency() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		final var jpy = Currency.getInstance("JPY");

		assertThrows(CurrencyNotAvailableException.class, () -> provider.getRates(USD, List.of(jpy)));
	}
}

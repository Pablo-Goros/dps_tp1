package edu.itba.dps.tp1.exchange.infrastructure.api;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.itba.dps.tp1.exchange.domain.exception.CurrencyNotAvailableException;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.io.http.HttpResponse;

@ExtendWith(MockitoExtension.class)
class CurrencyApiRateProviderTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency BRL = Currency.getInstance("BRL");
	private static final Currency EUR = Currency.getInstance("EUR");
	private static final Instant RETRIEVED_AT = Instant.parse("2026-08-31T12:00:00Z");

	private static final String RESPONSE_BODY = """
			{"data":{"BRL":5.4321,"EUR":0.8581081196}}""";

	@Mock
	private HttpClient httpClient;

	private CurrencyApiRateProvider provider;

	@BeforeEach
	void setUp() {
		provider = new CurrencyApiRateProvider(
				httpClient, "the-api-key", Clock.fixed(RETRIEVED_AT, ZoneOffset.UTC));
	}

	@Test
	void requestsTheLatestEndpointWithTheBaseAndTargetCurrencies() {
		when(httpClient.get(eq(URI.create("https://api.freecurrencyapi.com/v1/latest")),
				eq(Map.of("base_currency", "USD", "currencies", "BRL,EUR")), any()))
				.thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		provider.getRates(USD, List.of(BRL, EUR));
	}

	@Test
	void parsesCurrentRatesWithoutAnEffectiveDateAndWithTheSharedRetrievalTimestamp() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		final var rates = provider.getRates(USD, List.of(BRL, EUR));

		assertEquals(5.4321, rates.get(BRL).rate());
		assertEquals(0.8581081196, rates.get(EUR).rate());
		assertFalse(rates.get(BRL).effectiveDate().isPresent());
		assertFalse(rates.get(EUR).effectiveDate().isPresent());
		assertEquals(RETRIEVED_AT, rates.get(BRL).retrievedAt());
		assertEquals(RETRIEVED_AT, rates.get(EUR).retrievedAt());
	}

	@Test
	void failsWhenTheResponseIsMissingARequestedCurrency() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		final var jpy = Currency.getInstance("JPY");

		assertThrows(CurrencyNotAvailableException.class, () -> provider.getRates(USD, List.of(jpy)));
	}

	@Test
	void publicConstructorUsesTheSystemClock() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));
		final var systemClockProvider = new CurrencyApiRateProvider(httpClient, "the-api-key");

		final var before = Instant.now();
		final var retrievedAt = systemClockProvider.getRates(USD, List.of(BRL)).get(BRL).retrievedAt();
		final var after = Instant.now();

		assertFalse(retrievedAt.isBefore(before));
		assertFalse(retrievedAt.isAfter(after));
	}
}

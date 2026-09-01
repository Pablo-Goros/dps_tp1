package edu.itba.dps.tp1.exchange.infrastructure.api;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.io.http.HttpResponse;

@ExtendWith(MockitoExtension.class)
class CurrencyApiHistoricalRateProviderTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency EUR = Currency.getInstance("EUR");
	private static final Currency JPY = Currency.getInstance("JPY");
	private static final LocalDate DATE = LocalDate.of(2024, 11, 20);
	private static final Instant RETRIEVED_AT = Instant.parse("2026-08-31T12:00:00Z");

	private static final String RESPONSE_BODY = """
			{"data":{"2024-11-20":{"EUR":0.9480900974,"JPY":155.2721421669}}}""";

	@Mock
	private HttpClient httpClient;

	private CurrencyApiHistoricalRateProvider provider;

	@BeforeEach
	void setUp() {
		provider = new CurrencyApiHistoricalRateProvider(
				httpClient, "the-api-key", Clock.fixed(RETRIEVED_AT, ZoneOffset.UTC));
	}

	@Test
	void requestsTheHistoricalEndpointWithTheGivenDate() {
		when(httpClient.get(eq(URI.create("https://api.freecurrencyapi.com/v1/historical")),
				eq(Map.of("base_currency", "USD", "currencies", "EUR,JPY", "date", "2024-11-20")), any()))
				.thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		final var rates = provider.getRates(USD, List.of(EUR, JPY), DATE);

		assertEquals(0.9480900974, rates.get(EUR).rate());
		assertEquals(155.2721421669, rates.get(JPY).rate());
		assertEquals(java.util.Optional.of(DATE), rates.get(EUR).effectiveDate());
		assertEquals(java.util.Optional.of(DATE), rates.get(JPY).effectiveDate());
		assertEquals(RETRIEVED_AT, rates.get(EUR).retrievedAt());
		assertEquals(RETRIEVED_AT, rates.get(JPY).retrievedAt());
	}

	@Test
	void publicConstructorUsesTheSystemClock() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));
		final var systemClockProvider = new CurrencyApiHistoricalRateProvider(httpClient, "the-api-key");

		final var before = Instant.now();
		final var retrievedAt = systemClockProvider.getRates(USD, List.of(EUR), DATE).get(EUR).retrievedAt();
		final var after = Instant.now();

		org.junit.jupiter.api.Assertions.assertFalse(retrievedAt.isBefore(before));
		org.junit.jupiter.api.Assertions.assertFalse(retrievedAt.isAfter(after));
	}
}

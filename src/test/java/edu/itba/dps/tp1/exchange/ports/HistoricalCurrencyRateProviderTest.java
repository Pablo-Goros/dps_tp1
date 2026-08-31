package edu.itba.dps.tp1.exchange.ports;

import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoricalCurrencyRateProviderTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency EUR = Currency.getInstance("EUR");
	private static final LocalDate DATE = LocalDate.of(2024, 11, 20);
	private static final CurrencyRate RATE = new CurrencyRate(
			0.9, Optional.of(LocalDate.of(2024, 11, 20)), Instant.parse("2024-11-20T23:59:59Z"));

	@Test
	void getRateDefaultsToASingleEntryBatchLookup() {
		final HistoricalCurrencyRateProvider provider = (from, to, date) -> {
			assertEquals(USD, from);
			assertEquals(List.of(EUR), to);
			assertEquals(DATE, date);
			return Map.of(EUR, RATE);
		};

		assertEquals(RATE, provider.getRate(USD, EUR, DATE));
	}
}

package edu.itba.dps.tp1.exchange.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyRateTest {

	@Test
	void exposesRateEffectiveDateAndRetrievalTimestamp() {
		final var timestamp = Instant.parse("2024-11-20T23:59:59Z");
		final var date = LocalDate.of(2024, 11, 20);
		final var rate = new CurrencyRate(1.5, Optional.of(date), timestamp);

		assertEquals(1.5, rate.rate());
		assertEquals(Optional.of(date), rate.effectiveDate());
		assertEquals(timestamp, rate.retrievedAt());
	}

	@Test
	void rejectsNullEffectiveDate() {
		assertThrows(IllegalArgumentException.class, () -> new CurrencyRate(1.5, null, Instant.EPOCH));
	}

	@Test
	void rejectsNullRetrievalTimestamp() {
		assertThrows(IllegalArgumentException.class, () -> new CurrencyRate(1.5, Optional.empty(), null));
	}

	@Test
	void equalRatesAreEqual() {
		final var timestamp = Instant.parse("2024-11-20T23:59:59Z");

		final var rate = new CurrencyRate(1.5, Optional.empty(), timestamp);
		final var sameRate = new CurrencyRate(1.5, Optional.empty(), timestamp);
		assertEquals(rate, sameRate);
		assertEquals(rate.hashCode(), sameRate.hashCode());
		assertEquals(rate.toString(), sameRate.toString());
	}
}

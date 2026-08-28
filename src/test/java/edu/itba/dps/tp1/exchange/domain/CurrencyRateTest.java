package edu.itba.dps.tp1.exchange.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyRateTest {

	@Test
	void exposesRateAndTimestamp() {
		final var timestamp = Instant.parse("2024-11-20T23:59:59Z");
		final var rate = new CurrencyRate(1.5, timestamp);

		assertEquals(1.5, rate.rate());
		assertEquals(timestamp, rate.timestamp());
	}

	@Test
	void rejectsNullTimestamp() {
		assertThrows(IllegalArgumentException.class, () -> new CurrencyRate(1.5, null));
	}

	@Test
	void equalRatesAreEqual() {
		final var timestamp = Instant.parse("2024-11-20T23:59:59Z");

		assertEquals(new CurrencyRate(1.5, timestamp), new CurrencyRate(1.5, timestamp));
		assertEquals(new CurrencyRate(1.5, timestamp).hashCode(), new CurrencyRate(1.5, timestamp).hashCode());
		assertEquals(new CurrencyRate(1.5, timestamp).toString(), new CurrencyRate(1.5, timestamp).toString());
	}
}

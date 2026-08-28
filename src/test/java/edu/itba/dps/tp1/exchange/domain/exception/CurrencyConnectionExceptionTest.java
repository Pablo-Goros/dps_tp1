package edu.itba.dps.tp1.exchange.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyConnectionExceptionTest {

	@Test
	void wrapsTheOriginalCause() {
		final var cause = new RuntimeException("timeout");
		final var exception = new CurrencyConnectionException(cause);

		assertEquals(cause, exception.getCause());
		assertTrue(exception.getMessage().contains("currency exchange provider"));
	}
}

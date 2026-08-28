package edu.itba.dps.tp1.exchange.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyApiExceptionTest {

	@Test
	void exposesTheStatusCodeAndIncludesTheResponseBodyInTheMessage() {
		final var exception = new CurrencyApiException(500, "{\"error\":\"boom\"}");

		assertEquals(500, exception.statusCode());
		assertTrue(exception.getMessage().contains("500"));
		assertTrue(exception.getMessage().contains("boom"));
	}
}

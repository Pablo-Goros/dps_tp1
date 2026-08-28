package edu.itba.dps.tp1.exchange.io.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpConnectionExceptionTest {

	@Test
	void wrapsMessageAndCause() {
		final var cause = new RuntimeException("refused");
		final var exception = new HttpConnectionException("failed", cause);

		assertEquals("failed", exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
}

package edu.itba.dps.tp1.exchange.domain.exception;

import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyNotAvailableExceptionTest {

	@Test
	void mentionsTheMissingCurrencyInTheMessage() {
		final var exception = new CurrencyNotAvailableException(Currency.getInstance("JPY"));

		assertTrue(exception.getMessage().contains("JPY"));
	}
}

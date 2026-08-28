package edu.itba.dps.tp1.exchange.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConvertedAmountTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final CurrencyRate RATE = new CurrencyRate(1.5, Instant.parse("2024-11-20T23:59:59Z"));

	@Test
	void exposesCurrencyAmountAndRateUsed() {
		final var amount = new MoneyAmount(USD, 150);
		final var converted = new ConvertedAmount(USD, amount, RATE);

		assertEquals(USD, converted.currency());
		assertEquals(amount, converted.amount());
		assertEquals(RATE, converted.rateUsed());
	}

	@Test
	void rejectsNullCurrency() {
		assertThrows(IllegalArgumentException.class,
				() -> new ConvertedAmount(null, new MoneyAmount(USD, 150), RATE));
	}

	@Test
	void rejectsNullAmount() {
		assertThrows(IllegalArgumentException.class, () -> new ConvertedAmount(USD, null, RATE));
	}

	@Test
	void rejectsNullRateUsed() {
		assertThrows(IllegalArgumentException.class,
				() -> new ConvertedAmount(USD, new MoneyAmount(USD, 150), null));
	}

	@Test
	void equalConvertedAmountsAreEqual() {
		final var a = new ConvertedAmount(USD, new MoneyAmount(USD, 150), RATE);
		final var b = new ConvertedAmount(USD, new MoneyAmount(USD, 150), RATE);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a.toString(), b.toString());
	}
}

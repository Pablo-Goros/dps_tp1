package edu.itba.dps.tp1.exchange.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConvertedAmountTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final CurrencyRate RATE =
			new CurrencyRate(1.5, Optional.empty(), Instant.parse("2024-11-20T23:59:59Z"));

	@Test
	void exposesAmountTargetCurrencyAndRateUsed() {
		final var amount = new MoneyAmount(USD, 150);
		final var converted = new ConvertedAmount(amount, RATE);

		assertEquals(USD, converted.amount().currency());
		assertEquals(amount, converted.amount());
		assertEquals(RATE, converted.rateUsed());
	}

	@Test
	void rejectsNullAmount() {
		assertThrows(IllegalArgumentException.class, () -> new ConvertedAmount(null, RATE));
	}

	@Test
	void rejectsNullRateUsed() {
		assertThrows(IllegalArgumentException.class,
				() -> new ConvertedAmount(new MoneyAmount(USD, 150), null));
	}

	@Test
	void equalConvertedAmountsAreEqual() {
		final var a = new ConvertedAmount(new MoneyAmount(USD, 150), RATE);
		final var b = new ConvertedAmount(new MoneyAmount(USD, 150), RATE);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a.toString(), b.toString());
	}
}

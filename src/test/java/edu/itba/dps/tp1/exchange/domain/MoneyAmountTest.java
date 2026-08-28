package edu.itba.dps.tp1.exchange.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyAmountTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency ARS = Currency.getInstance("ARS");

	@Test
	void roundsAmountToTwoDecimalsWithHalfEvenRounding() {
		final var amount = new MoneyAmount(USD, new BigDecimal("10.005"));

		assertEquals(new BigDecimal("10.00"), amount.amount());
	}

	@Test
	void doubleConstructorDelegatesToBigDecimalConstructor() {
		final var amount = new MoneyAmount(USD, 10.5);

		assertEquals(new MoneyAmount(USD, new BigDecimal("10.50")), amount);
	}

	@Test
	void rejectsNullCurrency() {
		assertThrows(IllegalArgumentException.class, () -> new MoneyAmount(null, BigDecimal.TEN));
	}

	@Test
	void rejectsNullAmount() {
		assertThrows(IllegalArgumentException.class, () -> new MoneyAmount(USD, (BigDecimal) null));
	}

	@Test
	void addsAmountsInTheSameCurrency() {
		final var a = new MoneyAmount(USD, 10);
		final var b = new MoneyAmount(USD, 5);

		assertEquals(new MoneyAmount(USD, 15), a.add(b));
	}

	@Test
	void rejectsAddingDifferentCurrencies() {
		final var usd = new MoneyAmount(USD, 10);
		final var ars = new MoneyAmount(ARS, 10);

		assertThrows(IllegalArgumentException.class, () -> usd.add(ars));
	}

	@Test
	void multipliesTheUnroundedAmountByARate() {
		final var amount = new MoneyAmount(USD, 100);

		assertEquals(0, new BigDecimal("150.00").compareTo(amount.multiply(1.5)));
	}

	@Test
	void toStringAndEqualsAreConsistentForEqualAmounts() {
		final var a = new MoneyAmount(USD, 10);
		final var b = new MoneyAmount(USD, 10);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a.toString(), b.toString());
	}
}

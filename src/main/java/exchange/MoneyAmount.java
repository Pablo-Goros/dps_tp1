package edu.itba.class3.exchange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record MoneyAmount(Currency currency, BigDecimal amount) {

	public MoneyAmount {
		if (currency == null) {
			throw new IllegalArgumentException("Currency cannot be null");
		}
		if (amount == null) {
			throw new IllegalArgumentException("Amount cannot be null");
		}
		amount = amount.setScale(2, RoundingMode.HALF_EVEN);
	}

	public MoneyAmount(Currency currency, double amount) {
		this(currency, BigDecimal.valueOf(amount));
	}

	public MoneyAmount add(MoneyAmount other) {
		if (!this.currency.equals(other.currency)) {
			throw new IllegalArgumentException("Cannot add amounts with different currencies");
		}
		return new MoneyAmount(this.currency, this.amount.add(other.amount));
	}

	public BigDecimal multiply(double rate) {
		return this.amount.multiply(BigDecimal.valueOf(rate));
	}
}

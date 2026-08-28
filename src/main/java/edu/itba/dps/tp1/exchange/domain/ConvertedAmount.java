package edu.itba.dps.tp1.exchange.domain;

import java.util.Currency;

public record ConvertedAmount(Currency currency, MoneyAmount amount, CurrencyRate rateUsed) {

	public ConvertedAmount {
		if (currency == null) {
			throw new IllegalArgumentException("Currency cannot be null");
		}
		if (amount == null) {
			throw new IllegalArgumentException("Amount cannot be null");
		}
		if (rateUsed == null) {
			throw new IllegalArgumentException("Rate used cannot be null");
		}
	}
}

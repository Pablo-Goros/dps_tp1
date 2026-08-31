package edu.itba.dps.tp1.exchange.domain;

public record ConvertedAmount(MoneyAmount amount, CurrencyRate rateUsed) {

	public ConvertedAmount {
		if (amount == null) {
			throw new IllegalArgumentException("Amount cannot be null");
		}
		if (rateUsed == null) {
			throw new IllegalArgumentException("Rate used cannot be null");
		}
	}
}

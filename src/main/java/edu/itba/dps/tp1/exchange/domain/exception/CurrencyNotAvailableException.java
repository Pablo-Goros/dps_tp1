package edu.itba.dps.tp1.exchange.domain.exception;

import java.util.Currency;

public class CurrencyNotAvailableException extends CurrencyProviderException {

	public CurrencyNotAvailableException(Currency currency) {
		super("No exchange rate available for currency: " + currency.getCurrencyCode());
	}
}

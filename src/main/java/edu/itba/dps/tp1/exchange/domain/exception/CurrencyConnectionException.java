package edu.itba.dps.tp1.exchange.domain.exception;


public class CurrencyConnectionException extends CurrencyProviderException {

	public CurrencyConnectionException(Throwable cause) {
		super("Could not connect to the currency exchange provider", cause);
	}
}

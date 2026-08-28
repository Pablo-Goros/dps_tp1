package edu.itba.dps.tp1.exchange.domain.exception;

public abstract class CurrencyProviderException extends RuntimeException {

	protected CurrencyProviderException(String message) {
		super(message);
	}

	protected CurrencyProviderException(String message, Throwable cause) {
		super(message, cause);
	}
}

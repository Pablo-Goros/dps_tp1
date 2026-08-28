package edu.itba.dps.tp1.exchange.domain.exception;

public class CurrencyApiException extends CurrencyProviderException {

	private final int statusCode;

	public CurrencyApiException(int statusCode, String responseBody) {
		super("Currency exchange provider returned an error (status " + statusCode + "): " + responseBody);
		this.statusCode = statusCode;
	}

	public int statusCode() {
		return statusCode;
	}
}

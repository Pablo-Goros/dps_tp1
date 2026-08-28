package edu.itba.dps.tp1.exchange.domain;

import java.time.Instant;

public record CurrencyRate(double rate, Instant timestamp) {

	public CurrencyRate {
		if (timestamp == null) {
			throw new IllegalArgumentException("Timestamp cannot be null");
		}
	}
}

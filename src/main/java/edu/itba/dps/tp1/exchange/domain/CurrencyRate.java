package edu.itba.dps.tp1.exchange.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public record CurrencyRate(double rate, Optional<LocalDate> effectiveDate, Instant retrievedAt) {

	public CurrencyRate {
		if (effectiveDate == null) {
			throw new IllegalArgumentException("Effective date cannot be null");
		}
		if (retrievedAt == null) {
			throw new IllegalArgumentException("Retrieval timestamp cannot be null");
		}
	}
}

package edu.itba.dps.tp1.exchange.infrastructure.api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import edu.itba.dps.tp1.exchange.domain.exception.CurrencyNotAvailableException;


final class ExchangeRatesJson {

	private static final Gson GSON = new Gson();

	private ExchangeRatesJson() {
	}

	static String codesOf(List<Currency> currencies) {
		return currencies.stream().map(Currency::getCurrencyCode).collect(Collectors.joining(","));
	}

	static Map<Currency, CurrencyRate> parseLatestRates(
			String body, List<Currency> requested, Instant retrievedAt) {
		final LatestRatesResponse response = GSON.fromJson(body, LatestRatesResponse.class);
		return toCurrencyRates(response.data, requested, retrievedAt);
	}

	static Map<Currency, CurrencyRate> parseHistoricalRates(
			String body, List<Currency> requested, LocalDate date) {
		final HistoricalRatesResponse response = GSON.fromJson(body, HistoricalRatesResponse.class);
		final Instant timestamp = date.atStartOfDay(ZoneOffset.UTC).toInstant();
		return toCurrencyRates(response.data.get(date.toString()), requested, timestamp);
	}

	private static Map<Currency, CurrencyRate> toCurrencyRates(
			Map<String, Double> values, List<Currency> requested, Instant timestamp) {
		final Map<Currency, CurrencyRate> rates = new LinkedHashMap<>();
		for (Currency currency : requested) {
			final Double value = values.get(currency.getCurrencyCode());
			if (value == null) {
				throw new CurrencyNotAvailableException(currency);
			}
			rates.put(currency, new CurrencyRate(value, timestamp));
		}
		return rates;
	}

	private static final class LatestRatesResponse {
		private Map<String, Double> data;
	}

	private static final class HistoricalRatesResponse {
		private Map<String, Map<String, Double>> data;
	}
}

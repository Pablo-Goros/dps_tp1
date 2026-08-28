package edu.itba.dps.tp1.exchange.infrastructure.api;

import java.time.Instant;
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

	static Map<Currency, CurrencyRate> parseRates(String body, List<Currency> requested) {
		final ExchangeRateResponse response = GSON.fromJson(body, ExchangeRateResponse.class);
		final Instant timestamp = Instant.parse(response.meta.last_updated_at);
		final Map<Currency, CurrencyRate> rates = new LinkedHashMap<>();
		for (Currency currency : requested) {
			final CurrencyValue value = response.data.get(currency.getCurrencyCode());
			if (value == null) {
				throw new CurrencyNotAvailableException(currency);
			}
			rates.put(currency, new CurrencyRate(value.value, timestamp));
		}
		return rates;
	}

	private static final class ExchangeRateResponse {
		private Meta meta;
		private Map<String, CurrencyValue> data;
	}

	private static final class Meta {
		private String last_updated_at;
	}

	private static final class CurrencyValue {
		private double value;
	}
}

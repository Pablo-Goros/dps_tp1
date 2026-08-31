package edu.itba.dps.tp1.exchange.infrastructure.api;

import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.ports.HistoricalCurrencyRateProvider;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;

public class CurrencyApiHistoricalRateProvider implements HistoricalCurrencyRateProvider {

	private final CurrencyApiGateway gateway;
	private final Clock clock;

	public CurrencyApiHistoricalRateProvider(HttpClient httpClient, String apiKey) {
		this(httpClient, apiKey, Clock.systemUTC());
	}

	CurrencyApiHistoricalRateProvider(HttpClient httpClient, String apiKey, Clock clock) {
		this.gateway = new CurrencyApiGateway(httpClient, apiKey);
		this.clock = clock;
	}

	@Override
	public Map<Currency, CurrencyRate> getRates(Currency from, List<Currency> to, LocalDate date) {
		final String body = gateway.get("historical", Map.of(
				"base_currency", from.getCurrencyCode(),
				"currencies", ExchangeRatesJson.codesOf(to),
				"date", date.toString()));
		return ExchangeRatesJson.parseHistoricalRates(body, to, date, Instant.now(clock));
	}
}

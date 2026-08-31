package edu.itba.dps.tp1.exchange.infrastructure.api;

import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.ports.CurrencyRateProvider;

import java.time.Clock;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Map;

public class CurrencyApiRateProvider implements CurrencyRateProvider {

	private final CurrencyApiGateway gateway;
	private final Clock clock;

	public CurrencyApiRateProvider(HttpClient httpClient, String apiKey) {
		this(httpClient, apiKey, Clock.systemUTC());
	}

	CurrencyApiRateProvider(HttpClient httpClient, String apiKey, Clock clock) {
		this.gateway = new CurrencyApiGateway(httpClient, apiKey);
		this.clock = clock;
	}

	@Override
	public Map<Currency, CurrencyRate> getRates(Currency from, List<Currency> to) {
		final String body = gateway.get("latest", Map.of(
				"base_currency", from.getCurrencyCode(),
				"currencies", ExchangeRatesJson.codesOf(to)));
		return ExchangeRatesJson.parseLatestRates(body, to, Instant.now(clock));
	}
}

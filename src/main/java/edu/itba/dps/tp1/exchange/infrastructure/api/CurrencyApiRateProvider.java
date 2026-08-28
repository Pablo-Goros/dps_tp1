package edu.itba.dps.tp1.exchange.infrastructure.api;

import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.ports.CurrencyRateProvider;

import java.util.Currency;
import java.util.List;
import java.util.Map;

public class CurrencyApiRateProvider implements CurrencyRateProvider {

	private final CurrencyApiGateway gateway;

	public CurrencyApiRateProvider(HttpClient httpClient, String apiKey) {
		this.gateway = new CurrencyApiGateway(httpClient, apiKey);
	}

	@Override
	public Map<Currency, CurrencyRate> getRates(Currency from, List<Currency> to) {
		final String body = gateway.get("latest", Map.of(
				"base_currency", from.getCurrencyCode(),
				"currencies", ExchangeRatesJson.codesOf(to)));
		return ExchangeRatesJson.parseRates(body, to);
	}
}

package edu.itba.dps.tp1.exchange.infrastructure.api;

import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;

import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.ports.SupportedCurrencyProvider;

public class CurrencyApiSupportedCurrencyProvider implements SupportedCurrencyProvider {

	private static final Gson GSON = new Gson();

	private final CurrencyApiGateway gateway;

	public CurrencyApiSupportedCurrencyProvider(HttpClient httpClient, String apiKey) {
		this.gateway = new CurrencyApiGateway(httpClient, apiKey);
	}

	@Override
	public List<Currency> getSupportedCurrencies() {
		final String body = gateway.get("currencies", Map.of());
		final CurrenciesResponse response = GSON.fromJson(body, CurrenciesResponse.class);
		return response.data.keySet().stream()
				.flatMap(code -> toCurrency(code).stream())
				.sorted(Comparator.comparing(Currency::getCurrencyCode))
				.toList();
	}

	
	private Optional<Currency> toCurrency(String code) {
		try {
			return Optional.of(Currency.getInstance(code));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	private static final class CurrenciesResponse {
		private Map<String, Object> data;
	}
}

package edu.itba.dps.tp1.exchange.infrastructure.api;

import java.net.URI;
import java.util.Map;

import edu.itba.dps.tp1.exchange.domain.exception.CurrencyApiException;
import edu.itba.dps.tp1.exchange.domain.exception.CurrencyConnectionException;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.io.http.HttpConnectionException;
import edu.itba.dps.tp1.exchange.io.http.HttpResponse;

class CurrencyApiGateway {

	private static final URI BASE_URL = URI.create("https://api.currencyapi.com/v3/");

	private final HttpClient httpClient;
	private final String apiKey;

	CurrencyApiGateway(HttpClient httpClient, String apiKey) {
		this.httpClient = httpClient;
		this.apiKey = apiKey;
	}

	String get(String path, Map<String, Object> queryParams) {
		final HttpResponse response = executeGet(path, queryParams);
		if (response.statusCode() != 200) {
			throw new CurrencyApiException(response.statusCode(), response.body());
		}
		return response.body();
	}

	private HttpResponse executeGet(String path, Map<String, Object> queryParams) {
		try {
			return httpClient.get(BASE_URL.resolve(path), queryParams,
					Map.of("accept", "application/json", "apikey", apiKey));
		} catch (HttpConnectionException e) {
			throw new CurrencyConnectionException(e);
		}
	}
}

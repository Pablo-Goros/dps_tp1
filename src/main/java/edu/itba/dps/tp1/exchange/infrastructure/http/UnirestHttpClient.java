package edu.itba.dps.tp1.exchange.infrastructure.http;

import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.io.http.HttpConnectionException;
import edu.itba.dps.tp1.exchange.io.http.HttpResponse;
import kong.unirest.Unirest;

import java.net.URI;
import java.util.Map;

public class UnirestHttpClient implements HttpClient {

	@Override
	public HttpResponse get(URI url, Map<String, Object> queryParams, Map<String, String> headers) {
		try {
			final var response = Unirest.get(url.toString()).queryString(queryParams).headers(headers).asString();
			return new HttpResponse(response.getBody(), response.getStatus());
		} catch (final Exception e) {
			throw new HttpConnectionException("Failed to reach " + url, e);
		}
	}
}

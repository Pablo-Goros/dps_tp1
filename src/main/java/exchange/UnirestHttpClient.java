package edu.itba.class3.exchange;

import com.mashape.unirest.http.Unirest;
import edu.itba.class3.exchange.http.HttpClient;

import java.net.URI;
import java.util.Map;

public class UnirestHttpClient implements HttpClient {

	@Override
	public edu.itba.class3.exchange.http.HttpResponse get(final URI url, final Map<String, Object> queryParams,
			final Map<String, String> headers) {
		try {
			final var response = Unirest.get(url.toString()).queryString(queryParams).headers(headers).asJson();
			return new edu.itba.class3.exchange.http.HttpResponse(response.getBody().toString(), response.getStatus());
		} catch (final Exception e) {
			System.err.println("Error: " + e.getMessage());
			return new edu.itba.class3.exchange.http.HttpResponse("{\"error\":\"Internal Server Error\"}", 500);
		}
	}
}

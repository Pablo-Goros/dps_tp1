package edu.itba.class3.exchange.http;

import java.net.URI;
import java.util.Map;

public interface HttpClient {
	HttpResponse get(final URI url, Map<String, Object> queryParams, Map<String, String> headers);
}

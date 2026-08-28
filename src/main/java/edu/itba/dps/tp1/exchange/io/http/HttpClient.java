package edu.itba.dps.tp1.exchange.io.http;

import java.net.URI;
import java.util.Map;

public interface HttpClient {
	HttpResponse get(URI url, Map<String, Object> queryParams, Map<String, String> headers) throws HttpConnectionException;
}

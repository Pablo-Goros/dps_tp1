package edu.itba.dps.tp1.exchange.io.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpResponseTest {

	@Test
	void exposesBodyAndStatusCode() {
		final var response = new HttpResponse("{}", 200);

		assertEquals("{}", response.body());
		assertEquals(200, response.statusCode());
	}
}

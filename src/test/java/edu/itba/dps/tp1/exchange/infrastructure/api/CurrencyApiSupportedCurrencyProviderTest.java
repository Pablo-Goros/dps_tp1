package edu.itba.dps.tp1.exchange.infrastructure.api;

import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.itba.dps.tp1.exchange.io.http.HttpClient;
import edu.itba.dps.tp1.exchange.io.http.HttpResponse;

@ExtendWith(MockitoExtension.class)
class CurrencyApiSupportedCurrencyProviderTest {

	private static final String RESPONSE_BODY = """
			{"data":{"EUR":{"symbol":"\\u20ac","name":"Euro","code":"EUR"},"USD":{"symbol":"$","name":"US Dollar","code":"USD"},"XCOIN":{"symbol":"?","name":"Not a real currency","code":"XCOIN"}}}""";

	@Mock
	private HttpClient httpClient;

	private CurrencyApiSupportedCurrencyProvider provider;

	@BeforeEach
	void setUp() {
		provider = new CurrencyApiSupportedCurrencyProvider(httpClient, "the-api-key");
	}

	@Test
	void listsRecognizedCurrenciesSortedByCodeAndSkipsUnknownOnes() {
		when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse(RESPONSE_BODY, 200));

		final var currencies = provider.getSupportedCurrencies();

		assertEquals(List.of(Currency.getInstance("EUR"), Currency.getInstance("USD")), currencies);
	}
}

package edu.itba.class3.exchange;

import com.google.gson.Gson;
import edu.itba.class3.exchange.http.HttpClient;
import edu.itba.class3.exchange.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.util.Currency;
import java.util.Map;

@RequiredArgsConstructor
public class CurrencyApiProvider implements CurrencyRateProvider {

	private final HttpClient httpClient;

	@Override
	public CurrencyRate getCurrencyRate(Currency from, Currency to) {
		final var response = this.getConversionRate(from.getCurrencyCode(), to.getCurrencyCode());
		if (response.statusCode() == 200) {
			final var rate = this.getExchangeRateResponse(response);
			return new CurrencyRate(rate.getExchange(to.getCurrencyCode()));
		} else {
			throw new CurrencyRateNotAvailable();
		}
	}

	private ExchangeRateResponse getExchangeRateResponse(HttpResponse response) {
		return new Gson().fromJson(response.body(), ExchangeRateResponse.class);
	}

	private HttpResponse getConversionRate(String fromCurrency, String toCurrency) {
		return this.httpClient.get(URI.create("https://api.currencyapi.com/v3/latest"),
				Map.of("base_currency", fromCurrency, "currencies", toCurrency),
				Map.of("accept", "application/json", "apikey", "cur_live_33r7xmpvu4kiAmeK4XZrotAn84qT4SMnPlP9Use1"));
	}

	// Define a nested class to represent the response body.
	@Setter
	private static class ExchangeRateResponse {
		private Map<String, CurrencyData> data;

		public double getExchange(final String toCurrency) {
			final var currencyData = this.data.get(toCurrency);
			if (currencyData == null) {
				throw new IllegalStateException("Missing exchange rate for currency: " + toCurrency);
			}
			return currencyData.value;
		}

		private record CurrencyData(String code, double value) {
		}
	}

}

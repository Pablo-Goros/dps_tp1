package edu.itba.dps.tp1.exchange.main;

import edu.itba.dps.tp1.exchange.application.CurrencyManager;
import edu.itba.dps.tp1.exchange.infrastructure.api.CurrencyApiHistoricalRateProvider;
import edu.itba.dps.tp1.exchange.infrastructure.api.CurrencyApiRateProvider;
import edu.itba.dps.tp1.exchange.infrastructure.api.CurrencyApiSupportedCurrencyProvider;
import edu.itba.dps.tp1.exchange.infrastructure.http.UnirestHttpClient;
import edu.itba.dps.tp1.exchange.infrastructure.io.ConsoleWriter;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;

public class Main {

	public static void main(String[] args) {
		final String apiKey = new ApiKeyLoader().load();
		final HttpClient httpClient = new UnirestHttpClient();

		final CurrencyManager currencyManager = new CurrencyManager(
				new CurrencyApiRateProvider(httpClient, apiKey),
				new CurrencyApiHistoricalRateProvider(httpClient, apiKey),
				new CurrencyApiSupportedCurrencyProvider(httpClient, apiKey));

		new CurrencyConverterCli(currencyManager, new ConsoleWriter()).run();
	}
}

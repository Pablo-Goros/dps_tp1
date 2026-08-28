package edu.itba.dps.tp1.exchange.main;

import edu.itba.dps.tp1.exchange.application.CurrencyManager;
import edu.itba.dps.tp1.exchange.infrastructure.api.CurrencyApiHistoricalRateProvider;
import edu.itba.dps.tp1.exchange.infrastructure.api.CurrencyApiRateProvider;
import edu.itba.dps.tp1.exchange.infrastructure.api.CurrencyApiSupportedCurrencyProvider;
import edu.itba.dps.tp1.exchange.infrastructure.http.UnirestHttpClient;
import edu.itba.dps.tp1.exchange.infrastructure.io.ConsoleWriter;
import edu.itba.dps.tp1.exchange.io.http.HttpClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

	private static final String API_KEY_VAR = "CURRENCY_API_KEY";
	private static final Path DOTENV_FILE = Path.of(".env");

	public static void main(String[] args) {
		final String apiKey = requireApiKey();
		final HttpClient httpClient = new UnirestHttpClient();

		final CurrencyManager currencyManager = new CurrencyManager(
				new CurrencyApiRateProvider(httpClient, apiKey),
				new CurrencyApiHistoricalRateProvider(httpClient, apiKey),
				new CurrencyApiSupportedCurrencyProvider(httpClient, apiKey));

		new CurrencyConverterCli(currencyManager, new ConsoleWriter()).run();
	}

	/**
	 * Reads the API key from the real environment first; falls back to a
	 * ".env" file in the working directory for local convenience (dev-only
	 * shortcut, never read past this composition-root method).
	 */
	private static String requireApiKey() {
		final String apiKey = System.getenv(API_KEY_VAR) != null
				? System.getenv(API_KEY_VAR)
				: readFromDotEnvFile();
		if (apiKey == null || apiKey.isBlank()) {
			throw new IllegalStateException(
					"Missing " + API_KEY_VAR + ": set it as an environment variable or in a .env file");
		}
		return apiKey;
	}

	private static String readFromDotEnvFile() {
		if (!Files.isRegularFile(DOTENV_FILE)) {
			return null;
		}
		try {
			final List<String> lines = Files.readAllLines(DOTENV_FILE);
			return lines.stream()
					.map(String::strip)
					.filter(line -> line.startsWith(API_KEY_VAR + "="))
					.map(line -> line.substring((API_KEY_VAR + "=").length()).strip())
					.findFirst()
					.orElse(null);
		} catch (IOException e) {
			return null;
		}
	}
}

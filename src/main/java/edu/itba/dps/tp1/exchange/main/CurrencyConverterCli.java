package edu.itba.dps.tp1.exchange.main;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import edu.itba.dps.tp1.exchange.application.CurrencyManager;
import edu.itba.dps.tp1.exchange.domain.ConvertedAmount;
import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import edu.itba.dps.tp1.exchange.domain.MoneyAmount;
import edu.itba.dps.tp1.exchange.io.OutputWriter;


public class CurrencyConverterCli {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency BRL = Currency.getInstance("BRL");
	private static final Currency EUR = Currency.getInstance("EUR");
	private static final Currency JPY = Currency.getInstance("JPY");
	private static final LocalDate HISTORICAL_DATE = LocalDate.of(2024, 11, 20);

	private final CurrencyManager currencyManager;
	private final OutputWriter output;

	public CurrencyConverterCli(CurrencyManager currencyManager, OutputWriter output) {
		this.currencyManager = currencyManager;
		this.output = output;
	}

	public void run() {
		printSupportedCurrencies();
		printQuote();
		printSingleConversion();
		printMultiConversion();
		printHistoricalConversion();
	}

	private void printSupportedCurrencies() {
		output.write("Supported currencies: " + currencyManager.listSupportedCurrencies());
	}

	private void printQuote() {
		final CurrencyRate rate = currencyManager.getRate(USD, EUR);
		output.write("USD -> EUR rate: " + rate.rate() + " (retrieved at " + rate.retrievedAt() + ")");
	}

	private void printSingleConversion() {
		final ConvertedAmount converted = currencyManager.convert(new MoneyAmount(BRL, 100), USD);
		output.write(describe(converted));
	}

	private void printMultiConversion() {
		final List<ConvertedAmount> converted = currencyManager.convert(new MoneyAmount(USD, 100), List.of(EUR, JPY));
		converted.forEach(c -> output.write(describe(c)));
	}

	private void printHistoricalConversion() {
		final List<ConvertedAmount> converted =
				currencyManager.convert(new MoneyAmount(USD, 100), List.of(EUR, JPY), HISTORICAL_DATE);
		converted.forEach(c -> output.write(describe(c)));
	}

	private String describe(ConvertedAmount converted) {
		return converted.amount()
				+ " (rate used: " + converted.rateUsed().rate()
				+ converted.rateUsed().effectiveDate()
						.map(date -> ", effective date: " + date)
						.orElse("")
				+ ", retrieved at: " + converted.rateUsed().retrievedAt() + ")";
	}
}

package edu.itba.dps.tp1.exchange.application;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import edu.itba.dps.tp1.exchange.domain.ConvertedAmount;
import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import edu.itba.dps.tp1.exchange.domain.MoneyAmount;
import edu.itba.dps.tp1.exchange.ports.CurrencyRateProvider;
import edu.itba.dps.tp1.exchange.ports.HistoricalCurrencyRateProvider;
import edu.itba.dps.tp1.exchange.ports.SupportedCurrencyProvider;

public class CurrencyManager {

	private final CurrencyRateProvider rateProvider;
	private final HistoricalCurrencyRateProvider historicalRateProvider;
	private final SupportedCurrencyProvider supportedCurrencyProvider;

	public CurrencyManager(CurrencyRateProvider rateProvider,
			HistoricalCurrencyRateProvider historicalRateProvider,
			SupportedCurrencyProvider supportedCurrencyProvider) {
		this.rateProvider = rateProvider;
		this.historicalRateProvider = historicalRateProvider;
		this.supportedCurrencyProvider = supportedCurrencyProvider;
	}

	public List<Currency> listSupportedCurrencies() {
		return supportedCurrencyProvider.getSupportedCurrencies();
	}

	public CurrencyRate getRate(Currency from, Currency to) {
		return rateProvider.getRate(from, to);
	}

	public ConvertedAmount convert(MoneyAmount amount, Currency to) {
		return toConvertedAmount(amount, to, rateProvider.getRate(amount.currency(), to));
	}

	public List<ConvertedAmount> convert(MoneyAmount amount, List<Currency> to) {
		return toConvertedAmounts(amount, to, rateProvider.getRates(amount.currency(), to));
	}

	public List<ConvertedAmount> convert(MoneyAmount amount, List<Currency> to, LocalDate date) {
		return toConvertedAmounts(amount, to, historicalRateProvider.getRates(amount.currency(), to, date));
	}

	private List<ConvertedAmount> toConvertedAmounts(MoneyAmount amount, List<Currency> to,
			Map<Currency, CurrencyRate> rates) {
		return to.stream().map(currency -> toConvertedAmount(amount, currency, rates.get(currency))).toList();
	}

	private ConvertedAmount toConvertedAmount(MoneyAmount amount, Currency to, CurrencyRate rate) {
		return new ConvertedAmount(new MoneyAmount(to, amount.multiply(rate.rate())), rate);
	}
}

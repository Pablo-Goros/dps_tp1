package main.java.exchange;

import lombok.RequiredArgsConstructor;
import main.java.exchange.ConversionRate;

import java.util.Currency;

@RequiredArgsConstructor
public class CurrencyConverter {

	private final edu.itba.class3.exchange.CurrencyRateProvider currencyRateProvider;

	public MoneyAmount convert(MoneyAmount moneyAmount, Currency to) {
		ConversionRate rate = getRate(moneyAmount.currency(), to);
		return new MoneyAmount(to, moneyAmount.multiply(rate.getRate()));
	}

	public ConversionRate getRate(Currency from, Currency to) {
		return new ConversionRate(this.currencyRateProvider.getCurrencyRate(from, to).rate());
	}

}

package edu.itba.class3.exchange;

import java.util.Currency;

public interface CurrencyRateProvider {
	CurrencyRate getCurrencyRate(Currency from, Currency to);
}

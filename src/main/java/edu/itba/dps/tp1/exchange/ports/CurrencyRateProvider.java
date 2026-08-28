package edu.itba.dps.tp1.exchange.ports;

import java.util.Currency;
import java.util.List;
import java.util.Map;

import edu.itba.dps.tp1.exchange.domain.CurrencyRate;

public interface CurrencyRateProvider {

	Map<Currency, CurrencyRate> getRates(Currency from, List<Currency> to);

	default CurrencyRate getRate(Currency from, Currency to) {
		return getRates(from, List.of(to)).get(to);
	}
}

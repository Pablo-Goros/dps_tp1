package edu.itba.dps.tp1.exchange.ports;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import edu.itba.dps.tp1.exchange.domain.CurrencyRate;


public interface HistoricalCurrencyRateProvider {

	Map<Currency, CurrencyRate> getRates(Currency from, List<Currency> to, LocalDate date);

	default CurrencyRate getRate(Currency from, Currency to, LocalDate date) {
		return getRates(from, List.of(to), date).get(to);
	}
}

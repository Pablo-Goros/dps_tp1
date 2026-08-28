package edu.itba.dps.tp1.exchange.ports;

import java.util.Currency;
import java.util.List;

public interface SupportedCurrencyProvider {

	List<Currency> getSupportedCurrencies();
}

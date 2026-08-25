package edu.itba.class3;

import edu.itba.class3.exchange.CurrencyConverter;
import edu.itba.class3.exchange.CurrencyRate;
import edu.itba.class3.exchange.CurrencyRateProvider;
import edu.itba.class3.exchange.MoneyAmount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyConverterTest {

	public static final Currency USD = Currency.getInstance("USD");
	public static final Currency ARS = Currency.getInstance("ARS");

	@Test
	void testConvert() {
		// Given
		final var provider = mock(CurrencyRateProvider.class);
		when(provider.getCurrencyRate(ARS, USD)).thenReturn(new CurrencyRate(1.5));
		final var converter = new CurrencyConverter(provider);

		// When
		final var result = converter.convert(new MoneyAmount(ARS, 100), USD);

		// Then
		Assertions.assertEquals(new MoneyAmount(USD, 150), result);
	}
}
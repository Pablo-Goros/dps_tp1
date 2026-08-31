package edu.itba.dps.tp1.exchange.application;

import edu.itba.dps.tp1.exchange.domain.ConvertedAmount;
import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import edu.itba.dps.tp1.exchange.domain.MoneyAmount;
import edu.itba.dps.tp1.exchange.ports.CurrencyRateProvider;
import edu.itba.dps.tp1.exchange.ports.HistoricalCurrencyRateProvider;
import edu.itba.dps.tp1.exchange.ports.SupportedCurrencyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyManagerTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency ARS = Currency.getInstance("ARS");
	private static final Currency EUR = Currency.getInstance("EUR");
	private static final Currency JPY = Currency.getInstance("JPY");
	private static final Instant TIMESTAMP = Instant.parse("2024-11-20T23:59:59Z");
	private static final LocalDate DATE = LocalDate.of(2024, 11, 20);

	@Mock
	private CurrencyRateProvider rateProvider;
	@Mock
	private HistoricalCurrencyRateProvider historicalRateProvider;
	@Mock
	private SupportedCurrencyProvider supportedCurrencyProvider;

	private CurrencyManager manager;

	@BeforeEach
	void setUp() {
		manager = new CurrencyManager(rateProvider, historicalRateProvider, supportedCurrencyProvider);
	}

	@Test
	void listsSupportedCurrenciesThroughTheCatalogProvider() {
		when(supportedCurrencyProvider.getSupportedCurrencies()).thenReturn(List.of(USD, ARS));

		assertEquals(List.of(USD, ARS), manager.listSupportedCurrencies());
	}

	@Test
	void getsAQuoteWithoutConvertingAnyAmount() {
		final var rate = new CurrencyRate(1.5, Optional.empty(), TIMESTAMP);
		when(rateProvider.getRate(ARS, USD)).thenReturn(rate);

		assertEquals(rate, manager.getRate(ARS, USD));
	}

	@Test
	void convertsASingleAmountUsingTheRateFromTheProvider() {
		final var rate = new CurrencyRate(1.5, Optional.empty(), TIMESTAMP);
		when(rateProvider.getRate(ARS, USD)).thenReturn(rate);

		final var result = manager.convert(new MoneyAmount(ARS, 100), USD);

		assertEquals(new ConvertedAmount(new MoneyAmount(USD, 150), rate), result);
	}

	@Test
	void convertsTheSameAmountToMultipleCurrenciesAtOnce() {
		final var eurRate = new CurrencyRate(0.9, Optional.empty(), TIMESTAMP);
		final var jpyRate = new CurrencyRate(150, Optional.empty(), TIMESTAMP);
		when(rateProvider.getRates(USD, List.of(EUR, JPY))).thenReturn(Map.of(EUR, eurRate, JPY, jpyRate));

		final var result = manager.convert(new MoneyAmount(USD, 100), List.of(EUR, JPY));

		assertEquals(List.of(
				new ConvertedAmount(new MoneyAmount(EUR, 90), eurRate),
				new ConvertedAmount(new MoneyAmount(JPY, 15000), jpyRate)), result);
	}

	@Test
	void convertsToMultipleCurrenciesUsingAPastDateRate() {
		final var eurRate = new CurrencyRate(0.95, Optional.of(DATE), TIMESTAMP);
		final var jpyRate = new CurrencyRate(155, Optional.of(DATE), TIMESTAMP);
		when(historicalRateProvider.getRates(USD, List.of(EUR, JPY), DATE))
				.thenReturn(Map.of(EUR, eurRate, JPY, jpyRate));

		final var result = manager.convert(new MoneyAmount(USD, 100), List.of(EUR, JPY), DATE);

		assertEquals(List.of(
				new ConvertedAmount(new MoneyAmount(EUR, 95), eurRate),
				new ConvertedAmount(new MoneyAmount(JPY, 15500), jpyRate)), result);
	}
}

package edu.itba.dps.tp1.exchange.main;

import edu.itba.dps.tp1.exchange.application.CurrencyManager;
import edu.itba.dps.tp1.exchange.domain.ConvertedAmount;
import edu.itba.dps.tp1.exchange.domain.CurrencyRate;
import edu.itba.dps.tp1.exchange.domain.MoneyAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterCliTest {

	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency ARS = Currency.getInstance("ARS");
	private static final Currency EUR = Currency.getInstance("EUR");
	private static final Currency JPY = Currency.getInstance("JPY");
	private static final Instant TIMESTAMP = Instant.parse("2026-08-27T23:59:59Z");

	@Mock
	private CurrencyManager currencyManager;

	private final List<String> writtenMessages = new ArrayList<>();

	private CurrencyConverterCli cli;

	@BeforeEach
	void setUp() {
		cli = new CurrencyConverterCli(currencyManager, writtenMessages::add);
	}

	@Test
	void printsAllSevenFeaturesThroughTheOutputWriter() {
		final var eurRate = new CurrencyRate(0.9, TIMESTAMP);
		final var jpyRate = new CurrencyRate(150, TIMESTAMP);

		when(currencyManager.listSupportedCurrencies()).thenReturn(List.of(ARS, EUR, JPY, USD));
		when(currencyManager.getRate(USD, EUR)).thenReturn(eurRate);
		when(currencyManager.convert(eq(new MoneyAmount(ARS, 100)), eq(USD)))
				.thenReturn(new ConvertedAmount(USD, new MoneyAmount(USD, 10), eurRate));
		when(currencyManager.convert(eq(new MoneyAmount(USD, 100)), eq(List.of(EUR, JPY))))
				.thenReturn(List.of(
						new ConvertedAmount(EUR, new MoneyAmount(EUR, 90), eurRate),
						new ConvertedAmount(JPY, new MoneyAmount(JPY, 15000), jpyRate)));
		when(currencyManager.convert(eq(new MoneyAmount(USD, 100)), eq(List.of(EUR, JPY)), any(LocalDate.class)))
				.thenReturn(List.of(
						new ConvertedAmount(EUR, new MoneyAmount(EUR, 95), eurRate),
						new ConvertedAmount(JPY, new MoneyAmount(JPY, 15500), jpyRate)));

		cli.run();

		assertTrue(writtenMessages.get(0).contains("Supported currencies"));
		assertTrue(writtenMessages.get(1).contains("USD -> EUR rate"));
		assertTrue(writtenMessages.get(2).contains("rate used"));
		assertTrue(writtenMessages.stream().anyMatch(m -> m.contains("2024-11-20")));
		assertTrue(writtenMessages.size() >= 5);
	}
}

import edu.itba.class3.exchange.CurrencyApiProvider;
import edu.itba.class3.exchange.CurrencyConverter;
import edu.itba.class3.exchange.MoneyAmount;
import edu.itba.class3.exchange.UnirestHttpClient;

void main() {
	final var httpClient = new UnirestHttpClient();
	final var provider = new CurrencyApiProvider(httpClient);
	final var converter = new CurrencyConverter(provider);
	final var ars = new MoneyAmount(Currency.getInstance("ARS"), BigDecimal.valueOf(100.0));
	IO.println(converter.convert(ars, Currency.getInstance("USD")));
}

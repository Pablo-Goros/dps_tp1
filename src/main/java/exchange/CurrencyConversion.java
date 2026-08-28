package main.java.exchange;

import java.time.Instant;

public record CurrencyConversion {
    private MoneyAmount moneyAmount;
    private Instant time;
}

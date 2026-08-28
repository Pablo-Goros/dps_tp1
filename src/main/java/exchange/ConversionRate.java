package main.java.exchange;

import java.math.RoundingMode;

public record ConversionRate(double rate) {
    public ConversionRate {
        if (rate == null) {
            throw new IllegalArgumentException("Rate cannot be null");
        }
    }

}

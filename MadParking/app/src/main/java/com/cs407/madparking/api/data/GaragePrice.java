package com.cs407.madparking.api.data;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GaragePrice {
    private final double rate;

    public GaragePrice(String input) {
        if (input == null) {
            rate = 0;
            return;
        }

        // price is number after first $ after "Hourly Rate\n"
        Pattern pattern = Pattern.compile("Hourly Rate\\n\\$(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            rate = 0;
            return;
        }
        rate = Double.parseDouble(Objects.requireNonNull(matcher.group(1)));
    }

    public double getRate() {
        return rate;
    }
}
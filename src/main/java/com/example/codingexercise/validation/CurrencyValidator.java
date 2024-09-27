package com.example.codingexercise.validation;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CurrencyValidator {

    private static final List<String> validCurrencies = Arrays.asList("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD");

    public static void validate(String currency) {
        if (!validCurrencies.contains(currency)) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }
}
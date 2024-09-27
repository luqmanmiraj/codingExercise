package com.example.codingexercise.gateway.dto;

import java.util.Map;

public record CurrencyExchange(double amount, String base, String date, Map<String, Double> rates) {
}

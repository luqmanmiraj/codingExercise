package com.example.codingexercise.gateway;

import com.example.codingexercise.gateway.dto.CurrencyExchange;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class CurrencyServiceGateway {

    private final RestTemplate restTemplate;

    public CurrencyServiceGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double convertUsdTo(Double amount, String currency) {
        CurrencyExchange rate = restTemplate.getForObject( "https://api.frankfurter.app/latest?amount={amount}&from=USD&to={currency}", CurrencyExchange.class, amount, currency);
        if(rate != null && !CollectionUtils.isEmpty(rate.rates())) {
            return rate.rates().get(currency);
        }
        return 0.0;
    }
}

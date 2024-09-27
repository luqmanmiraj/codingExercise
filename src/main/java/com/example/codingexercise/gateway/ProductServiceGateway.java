package com.example.codingexercise.gateway;

import com.example.codingexercise.gateway.dto.Product;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class ProductServiceGateway {

    private final RestTemplate restTemplate;

    public ProductServiceGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Product getProduct(String id) {
        String url = "https://product-service.herokuapp.com/api/v1/products/{id}";
        HttpHeaders headers = createHeaders("user", "pass");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Product> response = restTemplate.exchange(url, HttpMethod.GET, entity, Product.class, id);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            // Handle the case where the product is not found
            return null;
        }
    }

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }
}

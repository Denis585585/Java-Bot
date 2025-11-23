package com.example.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CbrClient {
    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${cbr.currency.rates.xml.url}")
    private String cbrCurrencyRatesXmlUrl;

    public Optional<String> getCurrencyRates() throws RuntimeException {
        var request = new Request.Builder()
                .url(cbrCurrencyRatesXmlUrl)
                .build();
        try (var response = okHttpClient.newCall(request).execute()) {
            var body = response.body();
            return body == null ? Optional.empty() : Optional.of(body.string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

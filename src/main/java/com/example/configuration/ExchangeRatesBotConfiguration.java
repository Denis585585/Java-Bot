package com.example.configuration;

import com.example.bot.ExchangeRatesBot;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class ExchangeRatesBotConfiguration {

    //Новый бин, в котором регистрируется класс бота
    @Bean
    public TelegramBotsApi telegramBotsApi(ExchangeRatesBot exchangeRatesBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(exchangeRatesBot);
        api.registerBot(exchangeRatesBot);
        return api;
    }
    //конфигурируем бин, чтобы возможно было отправлять http-запросы
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}

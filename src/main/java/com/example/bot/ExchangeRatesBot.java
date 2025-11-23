package com.example.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {

    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }
//вызывается каждый раз, когда пользователь отправляет сообщение
    @Override
    public void onUpdateReceived(Update update) {
    }
//возвращает название бота
    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Value("${bot.name}")
    private String botUserName;
}

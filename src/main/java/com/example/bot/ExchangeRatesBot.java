package com.example.bot;

import com.example.exception.ServiceException;
import com.example.service.ExchangeRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {

    private final String botUserName;

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String START = "✅ Start";
    private static final String USD = "\uD83C\uDDFA\uD83C\uDDF8 USA";
    private static final String EUR = "\uD83C\uDDEA\uD83C\uDDFA EUR";
    private static final String CNY = "\uD83C\uDDE8\uD83C\uDDF3 CHY";
    private static final String RUSSIA = "₽"; // 🇷🇺
    private static final String HELP = "⚠️ Help";

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    public ExchangeRatesBot(@Value("${bot.token}") String botToken, @Value("${bot.name}") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (message) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case CNY -> cnyCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    }


    private void startCommand(Long chatId, String userName) {
        var text = """
                👋 Добро пожаловать в бот курсов валют, %s!
                
                %s Здесь Вы сможете узнать официальные курсы валют на сегодня.
                
                %s Доступные валюты:
                %s - Доллар США
                %s - Евро
                %s - Китайский юань
                
                %s Дополнительные команды:
                Help - справка
                """;
        var formattedText = String.format(text, userName,
                "ℹ️",
                "📋",
                USD, EUR, CNY,
                "📊");
        sendMessage(chatId, formattedText);
    }

    private void usdCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getUSDExchangeRate();
            var currentDate = LocalDate.now();
            var text = """
                    Курс доллара США
                    
                    📅 На дату: %s
                    
                    💵 1 USD = %s RUB %s
                    """;
            formattedText = String.format(text, currentDate.format(DATE), usd, RUSSIA);
        } catch (ServiceException e) {
            LOG.error("Error getting USD rate", e);
            formattedText = "Текущий курс доллара не получен, попробуйте позже";
        }
        sendMessage(chatId, formattedText);
    }

    private void eurCommand(Long chatId) {
        String formattedText;
        try {
            var eur = exchangeRatesService.getEURExchangeRate();
            var currentDate = LocalDate.now();
            var text = """
                    Курс евро
                    
                    📅 На дату: %s
                    
                    💶 1 EUR = %s RUB %s
                    """;
            formattedText = String.format(text, currentDate.format(DATE), eur, RUSSIA);
        } catch (ServiceException e) {
            LOG.error("Error getting EUR rate", e);
            formattedText = "Текущий евро не получен, попробуйте позже";
        }
        sendMessage(chatId, formattedText);
    }

    private void cnyCommand(Long chatId) {
        String formattedText;
        try {
            var cny = exchangeRatesService.getCNYExchangeRate();
            var currentDate = LocalDate.now();
            var text = """
                    Курс юаня
                    
                    📅 На дату: %s
                    
                    💴 1 CNY = %s RUB %s
                    """;
            formattedText = String.format(text, currentDate.format(DATE), cny, RUSSIA);
        } catch (ServiceException e) {
            LOG.error("Error getting CNY rate", e);
            formattedText = "Текущий юаня не получен, попробуйте позже";
        }
        sendMessage(chatId, formattedText);
    }


    private void helpCommand(Long chatId) {
        var text = """
                Для получения текущих курсов валют используйте команды: " +
                USD - курс доллара
                EUR - курс евро
                CNY - курс юаня
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Неизвестная команда!";
        sendMessage(chatId, text);
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        sendMessage.setReplyMarkup(replyKeyboardMarkup());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Error sending message", e);
        }
    }

    @Bean
    public ReplyKeyboardMarkup replyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add((new KeyboardButton(START)));
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add((new KeyboardButton(USD)));
        secondRow.add((new KeyboardButton(EUR)));
        secondRow.add((new KeyboardButton(CNY)));
        KeyboardRow thirdRow = new KeyboardRow();
        thirdRow.add(new KeyboardButton(HELP));
        keyboardRows.add(firstRow);
        keyboardRows.add(secondRow);
        keyboardRows.add(thirdRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}

package com.artostapyshyn.studLabbot.config;

import com.artostapyshyn.studLabbot.bot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@RequiredArgsConstructor
@Configuration
public class TelegramBotConfigBot {
    @Value("${telegram.bot.username}")
    String botName;
    @Value("${telegram.bot.token}")
    String botToken;

    @Bean
    public TelegramBot telegramChatBot() {
        return new TelegramBot(botToken, botName);
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramChatBot());
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't initialize bot ", e);
        }
    }
}
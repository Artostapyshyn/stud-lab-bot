package com.artostapyshyn.studLabbot.bot;

import com.artostapyshyn.studLabbot.handler.impl.StartCommandHandler;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Autowired
    private StartCommandHandler startCommandHandler;

    @Autowired
    private TelegramService telegramService;

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received update: {}", update);
        if(update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (text.equals("/start")) {
                startCommandHandler.execute(chatId, null);
            } else {
                telegramService.sendMessage(chatId, "Не відома команда: " + text);
            }

        } else {
            log.warn("Unexpected update from user");
        }
    }

    public void botConnect() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            botsApi.registerBot(this);
            log.info("Bot successfully started!");
        } catch (TelegramApiException e) {
            log.error("Error when starting bot. Details: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}

package com.artostapyshyn.studLabbot.bot;

import com.artostapyshyn.studLabbot.enums.UserState;
import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final Map<String, BotCommand> commandMap;
    private final Map<Long, UserState> userStates;
    private final TelegramService telegramService;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.username}") String botUsername,
                       @Qualifier("commandMap") Map<String, BotCommand> commandMap,
                       Map<Long, UserState> userStates, TelegramService telegramService) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.commandMap = commandMap;
        this.userStates = userStates;
        this.telegramService = telegramService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            UserState state = userStates.getOrDefault(chatId, UserState.IDLE);

            if(state == UserState.IDLE) {
                BotCommand command = commandMap.get(text);
                if (command != null) {
                    log.info("Executing command: {}", text);
                    command.execute(chatId, null);
                } else {
                    log.warn("Unknown command received: {}", text);
                    telegramService.sendMessage(chatId, "Невідома команда: " + text);
                }
            } else {
                BotCommand command = commandMap.get("Увійти \uD83D\uDD10");
                command.execute(chatId, new String[]{text});
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

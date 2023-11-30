package com.artostapyshyn.studLabbot.bot;

import com.artostapyshyn.studLabbot.enums.UserState;
import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Qualifier("commandMap")
    @Autowired
    private Map<String, BotCommand> commandMap;
    @Autowired
    private Map<Long, UserState> userStates;
    @Autowired
    private TelegramService telegramService;
    private final String botUserName;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken, @Value("${telegram.bot.username}") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            UserState state = userStates.getOrDefault(chatId, UserState.IDLE);

            if (state == UserState.IDLE) {
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

    @Override
    public String getBotUsername() {
        return botUserName;
    }

}

package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.UserRequestHandler;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.model.UserRequest;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Slf4j
@Component
@AllArgsConstructor
public class StartCommandHandler extends UserRequestHandler {

    private static final String COMMAND = "/start";

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        boolean applicable = isCommand(userRequest.getUpdate(), COMMAND);
        log.info("Checking if StartCommandHandler is applicable for user request: {}. Result: {}", userRequest, applicable);
        return applicable;
    }

    @Override
    public void handle(UserRequest request) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();
        telegramService.sendMessage(request.getChatId(),
                "\uD83D\uDC4BПривіт! За допомогою цього чат-бота ви зможете використовувати платформу StudLab!",
                replyKeyboard);
        telegramService.sendMessage(request.getChatId(),
                "Обирайте з меню нижче ⤵️");
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}

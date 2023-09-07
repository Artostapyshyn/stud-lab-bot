package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Slf4j
@Component
@AllArgsConstructor
public class StartCommandHandler implements BotCommand {

    private final TelegramService telegramService;

    private final KeyboardHelper keyboardHelper;

    @Override
    public void execute(Long chatId, String[] args) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();
        telegramService.sendMessage(chatId,
                "\uD83D\uDC4BПривіт! За допомогою цього чат-бота ви зможете використовувати платформу StudLab!", replyKeyboard);
        telegramService.sendMessage(chatId, "Обирайте з меню нижче ⤵️");
    }
}

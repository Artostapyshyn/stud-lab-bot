package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
@AllArgsConstructor
public class RulesCommandHandler implements BotCommand {

    private final TelegramService telegramService;

    private final KeyboardHelper keyboardHelper;

    @Override
    public void execute(Long chatId, String[] args) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();
        telegramService.sendMessage(chatId,
                "\n📌 До того, як розпочати, ми пропонуємо ознайомитися з правилами:\n" +
                        "\n📘 Переконайтеся, що ви розумієте, як працює наш сервіс та які обов'язки перед вами.\n" +
                        "\n🔗 <a href=\"https://studlab.in.ua/rules\">Натисніть тут, щоб переглянути правила</a> 🔗\n"
                , replyKeyboard);
    }
}

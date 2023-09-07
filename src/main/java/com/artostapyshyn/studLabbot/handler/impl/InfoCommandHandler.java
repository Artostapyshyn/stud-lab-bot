package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
@AllArgsConstructor
public class InfoCommandHandler implements BotCommand {

    private final TelegramService telegramService;

    private final KeyboardHelper keyboardHelper;

    @Override
    public void execute(Long chatId, String[] args) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();
        telegramService.sendMessage(chatId,
                "\uD83C\uDF93 <b>StudLab</b> \uD83C\uDF93\n" +
                        "Це онлайн-ресурс, який допомагає студентам з різних університетів у покращенні їхнього навчання та розвитку. Ця платформа надає студентам можливість отримати доступ до:\n" +
                        "\n" +
                        "\uD83D\uDCDA <b>Курсів:</b> \n" +
                        "Це розділ, який дозволяє студентам швидко і легко знайти цікаві та корисні курси, що пропонуються університетом чи іншими організаціями.\n" +
                        "\n" +
                        "\uD83C\uDF89 <b>Цікавих подій:</b> \n" +
                        "Існує безліч цікавих подій для студентів, які можуть допомогти їм розвинути свої здібності, знайти нових друзів та провести час з користю.\n", replyKeyboard);
    }

}

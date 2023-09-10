package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.model.UserToken;
import com.artostapyshyn.studLabbot.service.TelegramService;
import com.artostapyshyn.studLabbot.service.UserTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class LogoutCommandHandler implements BotCommand {

    private final Map<Long, String> userEmails;

    private final TelegramService telegramService;

    private final KeyboardHelper keyboardHelper;

    private final UserTokenService userTokenService;

    @Override
    public void execute(Long chatId, String[] args) {
        try {
            String email = userEmails.get(chatId);
            Optional<UserToken> existingUserTokenOpt = userTokenService.findByEmail(email);

            existingUserTokenOpt.ifPresent(userTokenService::delete);
            ReplyKeyboardMarkup loggedOutMenu = keyboardHelper.buildMainMenu();
            telegramService.sendMessage(chatId, "До зустрічі! \uD83D\uDC4B", loggedOutMenu);
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Помилка при вході: " + e.getMessage());
        }
    }
}
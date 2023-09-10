package com.artostapyshyn.studLabbot.helper;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class KeyboardHelper {

    public ReplyKeyboardMarkup buildMainMenu() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("Увійти \uD83D\uDD10");
        keyboardRow.add("Про StudLab");
        keyboardRow.add("Події");
        keyboardRow.add("Курси");
        keyboardRow.add("Правила сервісу");
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildLoggedInMenu() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("Профіль \uD83D\uDC64");
        keyboardRow.add("Про StudLab");
        keyboardRow.add("Події");
        keyboardRow.add("Курси");
        keyboardRow.add("Вийти \uD83D\uDEAA");
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
}

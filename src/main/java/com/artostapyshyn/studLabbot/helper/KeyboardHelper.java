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
        keyboardRow.add("Події");
        keyboardRow.add("Курси");
        keyboardRow.add("Правила сервісу \uD83D\uDCC4");
        keyboardRow.add("Про StudLab");
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

    public ReplyKeyboardMarkup buildProfileMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Профіль \uD83D\uDC64");
        row1.add("Друзі \uD83D\uDC65");
        row1.add("Редагувати профіль ✏\uFE0F");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Сповіщення \uD83D\uDD14");
        row2.add("Про StudLab");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Події");
        row3.add("Курси");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Вийти \uD83D\uDEAA");

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3, row4))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

}

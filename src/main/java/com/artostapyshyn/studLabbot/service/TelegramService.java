package com.artostapyshyn.studLabbot.service;

import com.artostapyshyn.studLabbot.sender.BotSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramService {

    private final BotSender botSender;

    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(text)
                .chatId(chatId.toString())
                .parseMode(ParseMode.HTML)
                .replyMarkup(replyKeyboard)
                .build();
        execute(sendMessage);
    }

    private void execute(BotApiMethod<?> botApiMethod) {
        try {
            botSender.execute(botApiMethod);
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
    }

    public void sendPhoto(Long chatId, byte[] photoBytes, String caption, ReplyKeyboard replyKeyboard) throws TelegramApiException {
        InputFile photo = new InputFile(new ByteArrayInputStream(photoBytes), "profile_photo.jpg");

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(photo)
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(replyKeyboard)
                .build();
        log.warn("Photo: " + photo);
        botSender.execute(sendPhoto);
    }

    public void sendMessageWithInlineKeyboard(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(text)
                .chatId(chatId.toString())
                .parseMode(ParseMode.HTML)
                .replyMarkup(replyKeyboard)
                .build();
        execute(sendMessage);
    }

}

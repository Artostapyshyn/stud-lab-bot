package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.model.UserToken;
import com.artostapyshyn.studLabbot.service.TelegramService;
import com.artostapyshyn.studLabbot.service.UserTokenService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.artostapyshyn.studLabbot.constants.ApiConstants.API_BASE_URL;

@Slf4j
@Component
@AllArgsConstructor
public class MessageCommandHandler implements BotCommand {
    private final RestTemplate restTemplate;

    private final TelegramService telegramService;

    private final KeyboardHelper keyboardHelper;

    private final UserTokenService userTokenService;

    private final ObjectMapper objectMapper;

    @Override
    public void execute(Long chatId, String[] args) {
        this.execute(chatId, args, true, null);
    }


    @SneakyThrows
    public void execute(Long chatId, String[] args, boolean sendNoNotificationMessage, LocalDateTime lastNotificationTime) {
        String url = API_BASE_URL + "messages/all";
        UserToken userToken = userTokenService.findByChatId(chatId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode responseBody = objectMapper.readTree(response.getBody());
            JsonNode messagesNode = responseBody.path("messages");
            if (messagesNode.isArray() && messagesNode.isEmpty() && sendNoNotificationMessage) {
                telegramService.sendMessage(chatId, "Сповіщення відсутні.");
            } else {
                for (JsonNode notification : messagesNode) {
                    String sentTimeString = notification.path("sentTime").asText();
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime notificationTime = LocalDateTime.parse(sentTimeString, formatter);
                        if (lastNotificationTime == null || notificationTime.isAfter(lastNotificationTime)) {
                            sendNotificationMessage(chatId, notification, keyboardHelper.buildProfileMenu());
                            updateLastNotificationTime(chatId, notificationTime);
                        }
                    } catch (DateTimeParseException e) {
                        log.error("Error parsing the notification sentTime: {}", sentTimeString, e);
                    }
                }
            }
        } else {
            telegramService.sendMessage(chatId, "Виникла помилка, спробуйте ще раз.");
        }
    }

    public void updateLastNotificationTime(Long chatId, LocalDateTime lastNotificationTime) {
        UserToken userToken = userTokenService.findByChatId(chatId);
        if (userToken != null) {
            userToken.setLastNotificationTime(lastNotificationTime);
            userTokenService.save(userToken);
        } else {
            log.error("UserToken not found for chatId: {}", chatId);
        }
    }

    public void sendNotificationMessage(Long chatId, JsonNode notification, ReplyKeyboard replyKeyboard) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton deleteFriendButton = new InlineKeyboardButton("Видалити повідомлення ❌");
        deleteFriendButton.setCallbackData("Видалити повідомлення" + notification.get("id").asLong());
        buttons.add(deleteFriendButton);

        inlineKeyboardMarkup.setKeyboard(List.of(buttons));
        String formattedNotificationMessage = formatNotificationMessage(notification);
        telegramService.sendMessageWithInlineKeyboard(chatId, formattedNotificationMessage, inlineKeyboardMarkup);
    }

    public String formatNotificationMessage(JsonNode notification) {
        String content = notification.path("content").asText();
        String sentTime = notification.path("sentTime").asText();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM HH:mm");
        String formattedDate = sentTime;

        try {
            Date date = inputFormat.parse(sentTime);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String clockEmoji = "\uD83D\uDD50";
        String messageEmoji = "\uD83D\uDCEC";

        return messageEmoji + content + "\n"
                + clockEmoji + " Відправлено: " + formattedDate;
    }

}

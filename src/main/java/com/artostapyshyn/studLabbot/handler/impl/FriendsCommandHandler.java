package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.model.UserToken;
import com.artostapyshyn.studLabbot.service.TelegramService;
import com.artostapyshyn.studLabbot.service.UserTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.artostapyshyn.studLabbot.constants.ApiConstants.API_BASE_URL;


@Slf4j
@Component
@AllArgsConstructor
public class FriendsCommandHandler implements BotCommand {

    private final TelegramService telegramService;
    private final RestTemplate restTemplate;
    private final UserTokenService userTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(Long chatId, String[] args) {
        UserToken userToken = userTokenService.findByChatId(chatId);
        String url = API_BASE_URL + "friends/all?studentId=" + userToken.getStudentId();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode friendsArray = objectMapper.readTree(response.getBody());
                for (JsonNode friend : friendsArray) {
                    String formattedProfileInfo = formatProfileInfo(friend);

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<InlineKeyboardButton> buttons = new ArrayList<>();

                    InlineKeyboardButton deleteFriendButton = new InlineKeyboardButton("Видалити друга ❌");
                    deleteFriendButton.setCallbackData("Видалити друга" + friend.get("friendId").asLong());
                    buttons.add(deleteFriendButton);

                    inlineKeyboardMarkup.setKeyboard(List.of(buttons));

                    telegramService.sendMessageWithInlineKeyboard(chatId, formattedProfileInfo, inlineKeyboardMarkup);
                }
            } else {
                telegramService.sendMessage(chatId, "Не вдалося отримати інформацію про друзів.");
            }
        } catch (RestClientException | JsonProcessingException e) {
            telegramService.sendMessage(chatId, "Помилка під час виконання запиту. Спробуйте ще раз");
        }
    }

    private static String formatProfileInfo(JsonNode profile) {
        return "👤 <b>" + profile.get("friendFirstName").asText() + " " + profile.get("friendLastName").asText() + "</b>" + "\n";
    }
}


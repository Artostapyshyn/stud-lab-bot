package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.enums.UserState;
import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.artostapyshyn.studLabbot.constants.ApiConstants.API_BASE_URL;

@Component
@AllArgsConstructor
public class LoginCommandHandler implements BotCommand {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final RestTemplate restTemplate;

    private final Map<Long, UserState> userStates;
    private final Map<Long, String> tempEmails = new HashMap<>();
    private final Map<Long, String> tempPasswords = new HashMap<>();

    @Override
    public void execute(Long chatId, String[] args) {
        try {
            UserState state = userStates.getOrDefault(chatId, UserState.IDLE);

            switch (state) {
                case IDLE -> {
                    telegramService.sendMessage(chatId, "Будь ласка, введіть ваш email:");
                    userStates.put(chatId, UserState.AWAITING_EMAIL);
                }
                case AWAITING_EMAIL -> {
                    if (args[0].contains("@")) {
                        tempEmails.put(chatId, args[0]);
                        telegramService.sendMessage(chatId, "Тепер введіть ваш пароль:");
                        userStates.put(chatId, UserState.AWAITING_PASSWORD);
                    } else {
                        telegramService.sendMessage(chatId, "Неправильний формат email. Будь ласка, введіть коректний email:");
                    }
                }
                case AWAITING_PASSWORD -> {
                    tempPasswords.put(chatId, args[0]);

                    String email = tempEmails.remove(chatId);
                    String password = tempPasswords.remove(chatId);

                    Map<String, String> map = new HashMap<>();
                    map.put("email", email);
                    map.put("password", password);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
                    ResponseEntity<Map> response = restTemplate.postForEntity(API_BASE_URL + "auth/login", request, Map.class);

                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().containsKey("token")) {
                        String token = (String) response.getBody().get("token");
                        telegramService.sendMessage(chatId, "Успішний вхід!");
                        keyboardHelper.buildLoggedInMenu();

                    } else {
                        telegramService.sendMessage(chatId, "Помилка при вході. Будь ласка, спробуйте знову.");
                    }
                    userStates.put(chatId, UserState.IDLE);
                }
            }
        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Помилка при вході: " + e.getMessage());
            userStates.remove(chatId);
            tempEmails.remove(chatId);
            tempPasswords.remove(chatId);
        }
    }
}

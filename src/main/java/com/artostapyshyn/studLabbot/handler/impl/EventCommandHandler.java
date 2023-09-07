package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.service.TelegramService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import static com.artostapyshyn.studLabbot.constants.ApiConstants.API_BASE_URL;

@Component
@AllArgsConstructor
public class EventCommandHandler implements BotCommand {

    private final RestTemplate restTemplate;

    private final TelegramService telegramService;

    private final KeyboardHelper keyboardHelper;

    @Override
    public void execute(Long chatId, String[] args) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();
        String url = API_BASE_URL + "events/all";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                telegramService.sendMessage(chatId, responseBody, replyKeyboard);
            } else {
                // Handle non-OK responses
            }

        } catch (RestClientException e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }

}

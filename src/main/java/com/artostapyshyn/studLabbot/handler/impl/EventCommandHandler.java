package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.service.TelegramService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;

    @Override
    public void execute(Long chatId, String[] args) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();
        String url = API_BASE_URL + "events/all";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode eventsArray = objectMapper.readTree(response.getBody());
                for (JsonNode event : eventsArray) {
                    sendEventMessage(chatId, event, replyKeyboard);
                }
            } else {
                telegramService.sendMessage(chatId, "Виникла помилка, спробуйте ще раз.");
            }

        } catch (RestClientException |  JsonProcessingException e ) {
            e.printStackTrace();
        }
    }

    public void sendEventMessage(Long chatId, JsonNode event, ReplyKeyboard replyKeyboard) {
        String formattedEventMessage = formatEventMessage(event);
        telegramService.sendMessage(chatId, formattedEventMessage, replyKeyboard);
    }

    public String formatEventMessage(JsonNode event) {
        StringBuilder message = new StringBuilder();

        message.append("📅 <b>").append(event.get("nameOfEvent").asText()).append("</b>\n\n");
        message.append("🕒 <b>Дата початку:</b> ").append(event.get("date").asText()).append("\n");
        message.append("🕒 <b>Дата завершення:</b> ").append(event.get("endDate").asText()).append("\n\n");
        message.append("📍 <b>Місце:</b> ").append(event.get("venue").asText()).append("\n\n");
        String description = event.get("description").asText();
        message.append(description).append("\n\n");
        return message.toString();
    }
}

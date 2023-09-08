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
public class CourseCommandHandler implements BotCommand {

    private final RestTemplate restTemplate;

    private final TelegramService telegramService;

    private final KeyboardHelper keyboardHelper;

    private final ObjectMapper objectMapper;

    @Override
    public void execute(Long chatId, String[] args) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();
        String url = API_BASE_URL + "course/all";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode eventsArray = objectMapper.readTree(response.getBody());
                for (JsonNode event : eventsArray) {
                    sendCourseMessage(chatId, event, replyKeyboard);
                }
            } else {
            }

        } catch (RestClientException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendCourseMessage(Long chatId, JsonNode event, ReplyKeyboard replyKeyboard) {
        String formattedCourseMessage = formatCourseMessage(event);
        telegramService.sendMessage(chatId, formattedCourseMessage, replyKeyboard);
    }

    public String formatCourseMessage(JsonNode event) {
        StringBuilder message = new StringBuilder();

        message.append("📅 <b>").append(event.get("courseName").asText()).append("</b>\n\n");
        String description = event.get("courseDescription").asText();
        message.append("\uD83D\uDD17 Посилання:</b> ").append(event.get("courseLink").asText()).append("\n\n");
        message.append(description).append("\n\n");
        return message.toString();
    }

}

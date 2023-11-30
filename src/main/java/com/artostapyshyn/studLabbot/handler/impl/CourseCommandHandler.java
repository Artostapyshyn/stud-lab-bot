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
                JsonNode coursesArray = objectMapper.readTree(response.getBody());
                for (JsonNode course : coursesArray) {
                    sendCourseMessage(chatId, course, replyKeyboard);
                }
            } else {
                telegramService.sendMessage(chatId, "Виникла помилка, спробуйте ще раз.");
            }
        } catch (RestClientException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendCourseMessage(Long chatId, JsonNode course, ReplyKeyboard replyKeyboard) {
        String formattedCourseMessage = formatCourseMessage(course);
        telegramService.sendMessage(chatId, formattedCourseMessage, replyKeyboard);
    }

    public String formatCourseMessage(JsonNode course) {
        StringBuilder message = new StringBuilder();

        String courseName = course.get("courseName").asText()
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        String courseLink = course.get("courseLink").asText()
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        message.append("📅 <b>").append(courseName).append("</b>\n\n");
        message.append("\uD83D\uDD17 <b>Посилання:</b> ").append(courseLink).append("\n\n");
        message.append(course.get("courseDescription").asText()).append("\n\n");

        return message.toString();
    }
}

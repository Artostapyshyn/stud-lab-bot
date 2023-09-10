package com.artostapyshyn.studLabbot.handler.impl;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.helper.KeyboardHelper;
import com.artostapyshyn.studLabbot.model.UserToken;
import com.artostapyshyn.studLabbot.service.TelegramService;
import com.artostapyshyn.studLabbot.service.UserTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

import static com.artostapyshyn.studLabbot.constants.ApiConstants.API_BASE_URL;

@Slf4j
@Component
@AllArgsConstructor
public class ProfileCommandHandler implements BotCommand {

    private final RestTemplate restTemplate;

    private final TelegramService telegramService;

    private final KeyboardHelper keyboardHelper;

    private final Map<Long, String> userEmails;

    private final UserTokenService userTokenService;

    @Override
    public void execute(Long chatId, String[] args) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildLoggedInMenu();
        String url = API_BASE_URL + "student/personal-info";

        String userEmail = userEmails.get(chatId);
        log.info(userEmail);
        try {
            UserToken user = userTokenService.findByEmail(userEmail).orElseThrow();
            String token = user.getToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Gson gson = new Gson();
                JsonArray profilesArray = gson.fromJson(response.getBody(), JsonArray.class);
                JsonObject profile = profilesArray.get(0).getAsJsonObject();
                sendProfileInfo(chatId, profile, replyKeyboard);
            } else {
                telegramService.sendMessage(chatId, "Виникла помилка, спробуйте ще раз.");
            }

        } catch (RestClientException e) {
            e.printStackTrace();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendProfileInfo(Long chatId, JsonObject profile, ReplyKeyboard replyKeyboard) throws TelegramApiException {
        String formattedProfileInfo = formatProfileInfo(profile);
        telegramService.sendMessage(chatId, formattedProfileInfo, replyKeyboard);
    }


    public String formatProfileInfo(JsonObject profile) {
        return
                "👤 <b>" + profile.get("firstName").getAsString() + " " +
                profile.get("lastName").getAsString() + "</b>" + "\n" +
                "🎓 <b>Спеціальність:</b> " + profile.get("major").getAsString() + "\n" +
                "📘 <b>Курс:</b> " + profile.get("course").getAsString() + "\n" +
                "🌍 <b>Місто:</b> " + profile.get("city").getAsString() + "\n\n" +
                "\uD83C\uDFEB <b>Університет:</b> " + profile.getAsJsonObject("university").get("name").getAsString() + "\n\n";
    }
}

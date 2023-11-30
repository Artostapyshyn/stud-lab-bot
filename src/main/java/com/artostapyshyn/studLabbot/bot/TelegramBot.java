package com.artostapyshyn.studLabbot.bot;

import com.artostapyshyn.studLabbot.enums.UserState;
import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.model.UserToken;
import com.artostapyshyn.studLabbot.service.TelegramService;
import com.artostapyshyn.studLabbot.service.UserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

import static com.artostapyshyn.studLabbot.constants.ApiConstants.API_BASE_URL;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private RestTemplate restTemplate;
    @Qualifier("commandMap")
    @Autowired
    private Map<String, BotCommand> commandMap;
    @Autowired
    private Map<Long, UserState> userStates;
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private UserTokenService userTokenService;
    private final String botUserName;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken, @Value("${telegram.bot.username}") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            Long friendId = null;

            try {
                friendId = Long.parseLong(callbackData.replace("Видалити друга", ""));
            } catch (NumberFormatException e) {
                log.error("Failed to parse friendId from callbackData: {}", callbackData);
            }

            if (callbackData.startsWith("Видалити повідомлення")) {
                Long messageId = Long.parseLong(callbackData.replace("Видалити повідомлення", "").trim());
                deleteMessage(messageId, callbackQuery.getMessage().getChatId());
                sendConfirmationMessage(callbackQuery.getMessage().getChatId(), "Повідомлення видалено.");
            }

            if (friendId != null) {
                boolean deleted = deleteFriend(friendId, callbackQuery.getMessage().getChatId());

                if (deleted) {
                    sendConfirmationMessage(callbackQuery.getMessage().getChatId(), "Друга видалено успішно.");
                } else {
                    sendConfirmationMessage(callbackQuery.getMessage().getChatId(), "Не вдалося видалити друга.");
                }
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            UserState state = userStates.getOrDefault(chatId, UserState.IDLE);

            if (state == UserState.IDLE) {
                BotCommand command = commandMap.get(text);
                if (command != null) {
                    log.info("Executing command: {}", text);
                    command.execute(chatId, null);
                } else {
                    log.warn("Unknown command received: {}", text);
                    telegramService.sendMessage(chatId, "Невідома команда: " + text);
                }
            } else {
                BotCommand command = commandMap.get("Увійти \uD83D\uDD10");
                command.execute(chatId, new String[]{text});
            }
        } else {
            log.warn("Unexpected update from user");
        }
    }

    private boolean deleteFriend(Long friendId, Long chatId) {
        UserToken userToken = userTokenService.findByChatId(chatId);
        String url = API_BASE_URL + "friends/delete-friend?friendId=" + friendId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (RestClientException e) {
            return false;
        }
    }

    private void deleteMessage(Long messageId, Long chatId) {
        UserToken userToken = userTokenService.findByChatId(chatId);
        String url = API_BASE_URL + "messages/delete-by-id?messageId=" + messageId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            response.getStatusCode();
        } catch (RestClientException e) {
            log.error("Error while deleting message", e);
        }
    }

    private void sendConfirmationMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending confirmation message", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }
}

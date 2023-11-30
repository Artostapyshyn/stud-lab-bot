package com.artostapyshyn.studLabbot.schedulers;

import com.artostapyshyn.studLabbot.handler.impl.MessageCommandHandler;
import com.artostapyshyn.studLabbot.model.UserToken;
import com.artostapyshyn.studLabbot.service.UserTokenService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class NotificationScheduler {

    private final MessageCommandHandler messageCommandHandler;

    private final UserTokenService userTokenService;

    @Scheduled(fixedRate = 60000)
    public void checkForNotifications() {
        List<UserToken> activeUsers = userTokenService.findAll();

        for (UserToken user : activeUsers) {
            messageCommandHandler.execute(user.getChatId(), null, false, user.getLastNotificationTime());
            user.setLastNotificationTime(LocalDateTime.now());
            userTokenService.save(user);
        }
    }
}


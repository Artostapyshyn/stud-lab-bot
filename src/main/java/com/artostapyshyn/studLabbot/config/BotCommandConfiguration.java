package com.artostapyshyn.studLabbot.config;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.handler.impl.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class BotCommandConfiguration {

    private final StartCommandHandler startCommandHandler;
    private final LoginCommandHandler loginCommandHandler;
    private final InfoCommandHandler infoCommandHandler;
    private final EventCommandHandler eventCommandHandler;
    private final CourseCommandHandler courseCommandHandler;
    private final ProfileCommandHandler profileCommandHandler;
    private final RulesCommandHandler rulesCommandHandler;
    private final LogoutCommandHandler logoutCommandHandler;
    private final FriendsCommandHandler friendsCommandHandler;
    private final MessageCommandHandler messageCommandHandler;
    private final EditProfileCommandHandler editProfileCommandHandler;

    @Bean
    public Map<String, BotCommand> commandMap() {
        Map<String, BotCommand> commands = new HashMap<>();
        commands.put("/start", startCommandHandler);
        commands.put("Про StudLab", infoCommandHandler);
        commands.put("Правила сервісу", rulesCommandHandler);
        commands.put("Увійти \uD83D\uDD10", loginCommandHandler);
        commands.put("Події", eventCommandHandler);
        commands.put("Курси", courseCommandHandler);
        commands.put("Профіль \uD83D\uDC64", profileCommandHandler);
        commands.put("Друзі \uD83D\uDC65", friendsCommandHandler);
        commands.put("Сповіщення \uD83D\uDD14", messageCommandHandler);
        commands.put("Вийти \uD83D\uDEAA", logoutCommandHandler);
        return commands;
    }
}

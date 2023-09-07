package com.artostapyshyn.studLabbot.config;

import com.artostapyshyn.studLabbot.handler.BotCommand;
import com.artostapyshyn.studLabbot.handler.impl.EventCommandHandler;
import com.artostapyshyn.studLabbot.handler.impl.InfoCommandHandler;
import com.artostapyshyn.studLabbot.handler.impl.LoginCommandHandler;
import com.artostapyshyn.studLabbot.handler.impl.StartCommandHandler;
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

    @Bean
    public Map<String, BotCommand> commandMap() {
        Map<String, BotCommand> commands = new HashMap<>();
        commands.put("/start", startCommandHandler);
        commands.put("Про StudLab", infoCommandHandler);
        commands.put("Увійти \uD83D\uDD10", loginCommandHandler);
        commands.put("Події", eventCommandHandler);
        return commands;
    }
}

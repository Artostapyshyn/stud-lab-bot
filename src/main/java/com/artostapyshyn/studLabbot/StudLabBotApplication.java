package com.artostapyshyn.studLabbot;

import com.artostapyshyn.studLabbot.bot.TelegramBot;
import com.artostapyshyn.studLabbot.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class StudLabBotApplication implements CommandLineRunner {

	@Lazy
    @Autowired
    private TelegramBot telegramBot;

    public static void main(String[] args) {
        SpringApplication.run(StudLabBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        telegramBot.botConnect();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Map<Long, UserState> userStates() {
        return new HashMap<>();
    }
}

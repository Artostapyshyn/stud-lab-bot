package com.artostapyshyn.studLabbot;

import com.artostapyshyn.studLabbot.enums.UserState;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class StudLabBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudLabBotApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Map<Long, UserState> userStates() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, String> userEmails() {
        return new HashMap<>();
    }
}

package com.artostapyshyn.studLabbot;

import com.artostapyshyn.studLabbot.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudLabBotApplication implements CommandLineRunner {

	@Autowired
	private TelegramBot telegramBot;

	public static void main(String[] args) {
		SpringApplication.run(StudLabBotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		telegramBot.botConnect();
	}
}

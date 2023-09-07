package com.artostapyshyn.studLabbot.handler;

public interface BotCommand {
    void execute(Long chatId, String[] args);
}

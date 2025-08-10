package org.example.telegrambot.bot;

import org.example.telegrambot.domain.CustomUser;

public class BotData {
    private TelegramBot bot;
    private CustomUser user;
    private String input;

    public BotData(TelegramBot bot, CustomUser user, String input) {
        this.bot = bot;
        this.user = user;
        this.input = input;
    }

    public static BotData of(TelegramBot bot, CustomUser user, String input) {
        return new BotData(bot, user, input);
    }

    public TelegramBot getBot() {
        return bot;
    }

    public CustomUser getUser() {
        return user;
    }

    public String getInput() {
        return input;
    }
}

package org.example.telegrambot.bot;

import org.example.telegrambot.domain.CustomUser;
import org.example.telegrambot.domain.UserData;

public class BotData {
    private TelegramBot bot;
    private CustomUser user;
    private String input;
    private UserData userData;

    public BotData(TelegramBot bot, CustomUser user, String input, UserData userData) {
        this.bot = bot;
        this.user = user;
        this.input = input;
        this.userData = userData;
    }

    public static BotData of(TelegramBot bot, CustomUser user, String input, UserData userData) {
        return new BotData(bot, user, input, userData);
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

    public UserData getUserData() {
        return userData;
    }
}

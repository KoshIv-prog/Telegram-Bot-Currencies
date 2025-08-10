package org.example.telegrambot.bot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.telegrambot.config.BotConfig;
import org.example.telegrambot.domain.CustomUser;
import org.example.telegrambot.service.CurrencyService;
import org.example.telegrambot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CurrencyService currencyService = new CurrencyService();


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        final String text;
        final Long chatId;
        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage() || update.getMessage().hasText()) {
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
        } else {
            return;
        }


        CustomUser user = UserService.getUser(chatId);

        BotData context;
        BotState state;


        if (user == null) {
            state = BotState.getInitialState();

            UserService.registerNewUser(chatId, state.ordinal());


            user = UserService.getUser(chatId);
            context = BotData.of(this, user, text);
            state.enter(context);
        } else {
            context = BotData.of(this, user, text);
            state = BotState.byId(user.getStatus());
        }

        state.handleInput(context);

        do {
            state = state.nextState();
            state.enter(context);
        } while (!state.isInputNeeded());

        UserService.setUserState(context, state.ordinal());
    }
}
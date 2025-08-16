package org.example.telegrambot.bot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.telegrambot.config.BotConfig;
import org.example.telegrambot.domain.CustomUser;
import org.example.telegrambot.domain.UserData;
import org.example.telegrambot.service.CurrencyService;
import org.example.telegrambot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Date;

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
        final String username;
        final String firstName ;
        final String lastName;
        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            username = update.getCallbackQuery().getFrom().getUserName();
            firstName = update.getCallbackQuery().getFrom().getFirstName();
            lastName = update.getCallbackQuery().getFrom().getLastName();
        } else if (update.hasMessage() || update.getMessage().hasText()) {
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            username = update.getMessage().getFrom().getUserName();
            firstName = update.getMessage().getFrom().getFirstName();
            lastName = update.getMessage().getFrom().getLastName();
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
            context = BotData.of(this, user, text, null);
            state.enter(context);
        } else {
            state = BotState.byId(user.getStatus());
            UserData userData = new UserData(null,user,firstName,lastName,username,new Date(),null,null);
            context = BotData.of(this, user, text, userData);

        }

        state.handleInput(context);

        do {
            state = state.nextState();
            state.enter(context);
        } while (!state.isInputNeeded());

        UserService.setUserState(context, state.ordinal());
    }
}
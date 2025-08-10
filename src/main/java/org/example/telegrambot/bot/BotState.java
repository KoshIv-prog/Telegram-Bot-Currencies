package org.example.telegrambot.bot;

import org.example.telegrambot.service.CurrencyService;
import org.example.telegrambot.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public enum BotState {
    HelloMSG(){
        @Override
        public void enter(BotData data) {
            String answer= "Привіт" ;
            executeMessage(data, answer, null);
        }

        @Override
        public BotState nextState() {
            return Start;
        }
    },


    Start{
        private BotState next;
        @Override
        public void enter(BotData data) {
            executeMessage(data,"Що будете робити",getButtons(sellOrBuyOpts, sellOrBuyOpts));
        }

        @Override
        public void handleInput(BotData data) {
            next = FirstValue;
            if(!sellOrBuyOpts.contains(data.getInput())){
                errorMessage(data,next);
                return;
            }

            if (data.equals("Продати")){
                UserService.setSellOrBuy(data, true);
            }else {
                UserService.setSellOrBuy(data, false);
            }
        }

        @Override
        public BotState nextState() {
            return BotState.FirstValue;
        }
    },


    FirstValue{
        private BotState next;
        @Override
        public void enter(BotData data) {
            executeMessage(data,"Виберіть валюту",getButtons(currenciesShow,currenciesData));
        }

        @Override
        public void handleInput(BotData data) {
            next = Amount;
            if(!currenciesData.contains(data.getInput())){
                errorMessage(data,next);
                return;
            }

            UserService.setUserCurrency(data, data.getInput(),true);
            executeMessage(data,"Перша валюта - "+data.getInput(), null);

        }

        @Override
        public BotState nextState() {
            return next;
        }
    },


    Amount{
        BotState next;
        @Override
        public void enter(BotData data) {
            executeMessage(data, "Виберіть кількість", getButtons(amountOpts, amountOpts));
        }

        @Override
        public void handleInput(BotData data) {
            if (!amountOpts.contains(data.getInput())){
                errorMessage(data,next);
                return;
            }
            if(data.getInput().equals("Custom")){
                next = CustomAmount;
                return;
            }
            next = SecondValue;
            UserService.setUserAmount(data, Double.valueOf(data.getInput()));
            executeMessage(data,"Кількість - "+data, null);
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },


    CustomAmount{
        BotState next;
        @Override
        public void enter(BotData data) {
            executeMessage(data,"Ведіть кількість вручу",null);
        }

        @Override
        public void handleInput(BotData data) {
            Double amount;
            try{
                amount = Double.valueOf(data.getInput());
            }catch (NumberFormatException e){
                errorMessage(data,next);
                return;
            }
            next = SecondValue;
            UserService.setUserAmount(data, amount);
            executeMessage(data,"Кількість - "+amount,null);
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },


    SecondValue{
        BotState next;
        @Override
        public void enter(BotData data) {
            executeMessage(data,"Ведіть наступну валюту",getButtons(currenciesShow,currenciesData));
        }

        @Override
        public void handleInput(BotData data) {
            next = Unswer;
            if(!currenciesData.contains(data.getInput())){
                errorMessage(data,next);
                return;
            }
            UserService.setUserCurrency(data, data.getInput(),false);
            executeMessage(data,"Друга валюта - "+data.getInput(), null);
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },


    Unswer(false){
        BotState next;
        @Override
        public void enter(BotData data) {
            try {
                executeMessage(data, CurrencyService.getCurrencyRates(data),null);
            } catch (IOException | ParseException e) {
                System.out.println(e.getMessage());
                errorMessage(data,next);
                return;
            }
            next = Start;
        }

        @Override
        public BotState nextState() {
            return next;
        }
    };



    //-----------------------------------//
    private static BotState[] states;
    private final boolean inputNeeded;

    BotState() {
        this.inputNeeded = true;
    }

    BotState(boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    public static BotState getInitialState() {
        return byId(0);
    }

    public static BotState byId(int id) {
        if (states == null) {
            states = BotState.values();
        }

        return states[id];
    }

    protected void executeMessage(BotData data, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(data.getUser().getChatId());
        sendMessage.setText(message);
        if (inlineKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        try {
            data.getBot().execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    protected InlineKeyboardMarkup getButtons(List<String> listKey, List<String> listValue){
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < listKey.size(); i++) {
            InlineKeyboardButton inline = new InlineKeyboardButton();
            inline.setText(listKey.get(i));
            inline.setCallbackData(listValue.get(i));
            keyboard.add(List.of(inline));
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    protected void errorMessage(BotData data, BotState nextState) {
        executeMessage(data,"Вибачте, сталася помилка, спробуйте знову",null);
        UserService.setUserState(data, 0);
        UserService.errorErase(data);

        nextState = Start;
    }

    public boolean isInputNeeded() {
        return inputNeeded;
    }

    public void handleInput(BotData data) {}
    public abstract void enter(BotData data);
    public abstract BotState nextState();

    //--------------------------------------------------------------------------------//
    static protected final List<String> currenciesData = List.of("USD","EUR","UAH","BGN","CZK");
    static protected final List<String> currenciesShow = List.of(
            "\uD83C\uDDFA\uD83C\uDDF8 USD \uD83C\uDDFA\uD83C\uDDF8",
            "\uD83C\uDDEA\uD83C\uDDFA EUR \uD83C\uDDEA\uD83C\uDDFA",
            "\uD83C\uDDFA\uD83C\uDDE6 UAH \uD83C\uDDFA\uD83C\uDDE6",
            "\uD83C\uDDE7\uD83C\uDDEC BGN \uD83C\uDDE7\uD83C\uDDEC",
            "\uD83C\uDDE8\uD83C\uDDFF CZK \uD83C\uDDE8\uD83C\uDDFF");
    static protected final List<String> sellOrBuyOpts = List.of("Продати","Купувати");
    static protected final List<String> amountOpts = List.of("1","10","50","100","Custom");


}

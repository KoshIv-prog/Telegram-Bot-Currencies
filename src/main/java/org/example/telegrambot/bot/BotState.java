package org.example.telegrambot.bot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.telegrambot.domain.UserData;
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

@Slf4j
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
            if (checkAdminCommand(data)==1){
                next = Admin;
                return;
            }else if (checkAdminCommand(data)==-1){
                next = errorMessage(data);
                return;
            }

            if(!sellOrBuyOpts.contains(data.getInput())){
                next = errorMessage(data);
                return;
            }
            next = FirstValue;

            if (data.equals("Продати")){
                UserService.setSellOrBuy(data, true);
            }else {
                UserService.setSellOrBuy(data, false);
            }
        }

        @Override
        public BotState nextState() {
            return next;
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
            if (checkAdminCommand(data)==1){
                next = Admin;
                return;
            }else if (checkAdminCommand(data)==-1){
                next = errorMessage(data);
                return;
            }

            if(!currenciesData.contains(data.getInput())){
                next = errorMessage(data);
                return;
            }

            next = Amount;
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
            if (checkAdminCommand(data)==1){
                next = Admin;
                return;
            }else if (checkAdminCommand(data)==-1){
                next = errorMessage(data);
                return;
            }

            Double amount;
            if (!amountOpts.contains(data.getInput())){
                next = errorMessage(data);
                return;
            }
            if(data.getInput().equals("Custom")){
                next = CustomAmount;
                return;
            }

            try {
                amount = Double.valueOf(data.getInput());
                System.out.println(amount);
            }catch (NumberFormatException e){
                System.out.println(e.getMessage());
                next = errorMessage(data);
                return;
            }

            next = SecondValue;
            UserService.setUserAmount(data,amount);
            executeMessage(data,"Кількість - "+amount, null);
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
            if (checkAdminCommand(data)==1){
                next = Admin;
            }else if (checkAdminCommand(data)==-1){
                next = errorMessage(data);
                return;
            }

            Double amount;
            try{
                amount = Double.valueOf(data.getInput());
            }catch (NumberFormatException e){
                next = errorMessage(data);
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
            if (checkAdminCommand(data)==1){
                next = Admin;
            }else if (checkAdminCommand(data)==-1){
                next = errorMessage(data);
                return;
            }

            if(!currenciesData.contains(data.getInput())){
                next = errorMessage(data);
                return;
            }

            next = Unswer;
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
            String unswer = "";
            try {
                unswer = CurrencyService.getCurrencyRates(data);
            } catch (Exception e) {
                next = errorMessage(data);
                return;
            }
            executeMessage(data, unswer, null);
            next = Start;
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },
    Admin{
        BotState next;
        @Override
        public void enter(BotData data) {
            executeMessage(data,"Це панель адміністратора"+System.lineSeparator()+"Що хочете дізнатися?",getButtons(adminOpts,adminOpts));
        }

        @Override
        public void handleInput(BotData data) {
            if (!adminOpts.contains(data.getInput())){
                errorMessage(data);
                next = Admin;
            }

            if (data.getInput().equals("Показати всі обрахунки")){
                UserService.getAllCounts().forEach(count->{
                    executeMessage(data,
                            "Час - "+count.getDate()+System.lineSeparator()+
                                    "Імя - "+count.getFirstName()+System.lineSeparator()+
                                    "Фамілія - "+count.getLastName()+System.lineSeparator()+
                                    "Username - "+count.getUsername()+System.lineSeparator()+
                                    count.getTransaction()
                            ,null);
                });
                next = Admin;
            }else if (data.getInput().equals("Показати обрахунки кристувача")){
                next = UserCounts;
            }else if (data.getInput().equals("Показати нові обрахунки")){
                List<UserData> counts = UserService.getAllNewCounts();
                if (counts.isEmpty()){
                    executeMessage(data,"Жодних нових повідомлень",null);
                    next = Admin;
                    return;
                }

                counts.forEach(count->{
                    executeMessage(data,
                            "Час - "+count.getDate()+System.lineSeparator()+
                                    "Імя - "+count.getFirstName()+System.lineSeparator()+
                                    "Фамілія - "+count.getLastName()+System.lineSeparator()+
                                    "Username - "+count.getUsername()+System.lineSeparator()+
                                    count.getTransaction()
                            ,null);
                });
                next = Admin;
            }else if (data.getInput().equals("Кількість користувачів")){
                executeMessage(data, "Кількість користувачів - "+UserService.countAllUsers(),null);
                next = Admin;
            }else if (data.getInput().equals("Повернутися")){
                next = Start;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },
    UserCounts{
        @Override
        public void enter(BotData data) {
            executeMessage(data, "Ведіть username користувача",null);
        }

        @Override
        public void handleInput(BotData data) {
            List<UserData> userCounts = UserService.getAllUserCounts(data);

            if (userCounts.isEmpty()){
                executeMessage(data,"Немає повідомлень від користувача",null);
                return;
            }

            userCounts.forEach(count->{
                executeMessage(data,
                        "Час - "+count.getDate()+System.lineSeparator()+
                                "Імя - "+count.getFirstName()+System.lineSeparator()+
                                "Фамілія - "+count.getLastName()+System.lineSeparator()+
                                "Username - "+count.getUsername()+System.lineSeparator()+
                                count.getTransaction()
                        ,null);
            });
        }

        @Override
        public BotState nextState() {
            return Admin;
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

    @Setter
    private static String password;

    protected Integer checkAdminCommand(BotData data) {
        String msg = data.getInput();
        if (!msg.contains("/Admin")){
            return 0;
        }
        if (password != null && msg.contains(password)){
            executeMessage(data, "Панель адміна" ,null);
            return 1;
        }
        return -1;
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

    protected BotState errorMessage(BotData data) {
        executeMessage(data,"Вибачте, сталася помилка, спробуйте знову",null);
        UserService.setUserState(data, 0);
        UserService.errorErase(data);

        return Start;
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
    static protected final List<String> adminOpts = List.of(
            "Показати всі обрахунки",
            "Показати обрахунки кристувача",
            "Показати нові обрахунки",
            "Кількість користувачів",
            "Повернутися");


}

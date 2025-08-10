package org.example.telegrambot.service;

import org.example.telegrambot.bot.BotData;
import org.example.telegrambot.domain.CustomUser;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.*;


public class CurrencyService {


    public static String getCurrencyRates(BotData data) throws IOException, ParseException {
        CustomUser user = UserService.getUser(data.getUser().getChatId());

        String currency1;
        String currency2;
        Double amount = user.getAmount();

        if (!user.getSell()) {
            currency1 = user.getFirstValue().toLowerCase().replace(" ", "");
            currency2 = user.getSecondValue().toLowerCase().replace(" ", "");
        }else {
            currency2 = user.getFirstValue().toLowerCase().replace(" ", "");
            currency1 = user.getSecondValue().toLowerCase().replace(" ", "");
        }

        URL url = new URL("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/"+currency1+".json");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();

        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine());
        }
        scanner.close();

        JSONObject jsonObject = new JSONObject(result.toString());

        JSONObject uahObject = jsonObject.getJSONObject(currency1.toLowerCase());
        Map<String, Double> currencyMap = new HashMap<>();
        for (String key : uahObject.keySet()) {
            currencyMap.put(key, uahObject.getDouble(key));
        }



        return "По курсу "+ currency1.toUpperCase()+ " до "+ currency2.toUpperCase() + System.lineSeparator()+
                "Є : "+amount+" "+ currency1.toUpperCase() + " за " + currencyMap.get(currency2)*amount+" " + currency2.toUpperCase();

    }

}

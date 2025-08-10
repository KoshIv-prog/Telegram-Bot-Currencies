package org.example.telegrambot.service;

import org.example.telegrambot.bot.BotData;
import org.example.telegrambot.domain.CustomUser;
import org.example.telegrambot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static UserRepository userRepository = null;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static CustomUser getUser(Long chatId) {
        return userRepository.findUserByChatId(chatId);
    }

    public static void registerNewUser(Long chatId, Integer state) {
        userRepository.save(new CustomUser(chatId, state,  "", "",false));
    }

    public static void errorErase(BotData data) {
        CustomUser user = userRepository.findUserByChatId(data.getUser().getChatId());

        user.setFirstValue("");
        user.setSecondValue("");
        user.setAmount(0.0);
        user.setStatus(1);

        userRepository.save(user);
    }

    public static void setUserState(BotData data, Integer state) {
        CustomUser user = userRepository.findUserByChatId(data.getUser().getChatId());
        user.setStatus(state);
        userRepository.save(user);
    }
    public static void setUserCurrency(BotData data, String currency, boolean isFirst) {
        CustomUser user = userRepository.findUserByChatId(data.getUser().getChatId());
        if (isFirst) {
            user.setFirstValue(currency);
        }else {
            user.setSecondValue(currency);
        }
        userRepository.save(user);
    }
    public static void setUserAmount(BotData data, Double amount) {
        CustomUser user = userRepository.findUserByChatId(data.getUser().getChatId());
        user.setAmount(amount);
        userRepository.save(user);
    }
    public static void setSellOrBuy(BotData data, Boolean sellOrBuy) {
        CustomUser user = userRepository.findUserByChatId(data.getUser().getChatId());
        user.setSell(sellOrBuy);
        userRepository.save(user);
    }
}

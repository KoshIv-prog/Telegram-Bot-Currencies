package org.example.telegrambot.service;

import lombok.Getter;
import org.example.telegrambot.bot.BotData;
import org.example.telegrambot.domain.CustomUser;
import org.example.telegrambot.domain.UserData;
import org.example.telegrambot.repository.UserDataRepository;
import org.example.telegrambot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static UserRepository userRepository = null;
    @Getter
    private static UserDataRepository userDataRepository = null;

    public UserService(UserRepository userRepository,UserDataRepository userDataRepository) {
        UserService.userRepository = userRepository;
        UserService.userDataRepository = userDataRepository;

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

    public static List<UserData> getAllCounts() {
        List<UserData> userDataList = userDataRepository.findAll();
        for (UserData userData : userDataList) {
            userData.setNotified(true);
            userDataRepository.save(userData);
        }

        return userDataList;
    }

    public static List<UserData> getAllNewCounts() {
        List<UserData> userDataList = userDataRepository.findAllByNotified(false);
        for (UserData userData : userDataList) {
            userData.setNotified(true);
            userDataRepository.save(userData);
        }
        return userDataList;
    }
    public static long countAllUsers() {
        return userRepository.count();
    }
    public static List<UserData> getAllUserCounts(BotData data) {
        List<UserData> userDataList = userDataRepository.findAllByUsername(data.getInput().replace(" ",""));
        for (UserData userData : userDataList) {
            userData.setNotified(true);
            userDataRepository.save(userData);
        }
        return userDataList;
    }
}

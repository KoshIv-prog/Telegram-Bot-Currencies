package org.example.telegrambot;

import org.example.telegrambot.bot.BotState;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Scanner;

@SpringBootApplication
@EntityScan(basePackages = "org.example.telegrambot.domain")
public class TelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        BotState.setPassword(password);
        scanner.close();
    }

}

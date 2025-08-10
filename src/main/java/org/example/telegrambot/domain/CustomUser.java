package org.example.telegrambot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long chatId;
    private Integer status;
    private String firstValue;
    private String secondValue;
    private Boolean sell;
    private Double amount;

    public CustomUser(Long chatId, Integer status, String firstValue, String secondValue, Boolean sell) {
        this.chatId = chatId;
        this.status = status;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.sell = sell;
    }

    public CustomUser() {

    }
}

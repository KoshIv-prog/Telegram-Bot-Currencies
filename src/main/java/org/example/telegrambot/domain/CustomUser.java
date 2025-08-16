package org.example.telegrambot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "custom_user")
@NoArgsConstructor
@Setter
@Getter
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "`first_value`")
    private String firstValue;

    @Column(name = "`second_value`")
    private String secondValue;

    @Column(name = "sell")
    private Boolean sell;

    @Column(name = "amount")
    private Double amount;

    public CustomUser(Long chatId, Integer status, String firstValue, String secondValue, Boolean sell) {
        this.chatId = chatId;
        this.status = status;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.sell = sell;
    }

}

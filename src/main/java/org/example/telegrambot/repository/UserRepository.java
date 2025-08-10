package org.example.telegrambot.repository;


import org.example.telegrambot.domain.CustomUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<CustomUser, Long> {
    @EntityGraph(attributePaths = {"status", "firstValue", "secondValue", "sell", "amount"})
    CustomUser findUserByChatId(Long chatId);
}

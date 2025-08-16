package org.example.telegrambot.repository;


import org.example.telegrambot.domain.CustomUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<CustomUser, Long> {
    @EntityGraph(attributePaths = {"status", "firstValue", "secondValue", "sell", "amount"})
    CustomUser findUserByChatId(Long chatId);
}

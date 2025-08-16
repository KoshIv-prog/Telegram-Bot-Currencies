package org.example.telegrambot.repository;

import org.example.telegrambot.domain.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDataRepository extends JpaRepository<UserData, Long> {

    List<UserData> findAllByNotified(Boolean notified);

    List<UserData> findAllByUsername(String username);
}

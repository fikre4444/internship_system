package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.ChatId;
import java.util.Optional;

public interface ChatIdRepository extends JpaRepository<ChatId, Long> {

  Optional<ChatId> findByChatId(Long chatId);

}

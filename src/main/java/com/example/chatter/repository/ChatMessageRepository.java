package com.example.chatter.repository;

import com.example.chatter.entity.ChatMessage;
import java.sql.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Timestamp> {
}

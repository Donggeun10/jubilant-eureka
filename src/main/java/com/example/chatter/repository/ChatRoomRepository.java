package com.example.chatter.repository;

import com.example.chatter.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

}

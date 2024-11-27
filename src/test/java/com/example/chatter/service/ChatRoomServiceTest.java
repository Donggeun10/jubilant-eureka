package com.example.chatter.service;

import com.example.chatter.entity.ChatRoom;
import com.example.chatter.exception.NotFoundChatRoomException;
import com.example.chatter.exception.NotFoundChatRoomMemberException;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    private List<String> memberIds = List.of("apple", "banana");
    private String roomId = UUID.randomUUID().toString();

    @BeforeEach
    void setup() {
        ChatRoom chatRoom = chatRoomService.createRoom("testRooom", memberIds);
        roomId = chatRoom.getRoomId();

        chatRoomService.joinRoom(roomId, memberIds.getFirst());
    }

    @Test
    void testJoinRoom() {

        String memberId = "apple_juice";
        try {
            chatRoomService.joinRoom(roomId, memberId);
        }catch (NotFoundChatRoomException e) {
            log.error("error: {}", e.getMessage());
        }

    }

    @Test
    void testLeaveRoom() {

        try {
            chatRoomService.leaveRoom(roomId, memberIds.getFirst());
        }catch (NotFoundChatRoomException | NotFoundChatRoomMemberException e) {
            log.error("error: {}", e.getMessage());
        }

    }
}

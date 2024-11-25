package com.example.chatter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.chatter.entity.ChatRoom;
import com.example.chatter.service.ChatRoomService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatRoomService chatRoomService;

    private String testRoomId = "";

    @BeforeEach
    void setup() {
        String name = "테스트 채팅방";
        List<String> memberIds = List.of("user1", "user2");

        ChatRoom chatRoom = chatRoomService.createRoom(name, memberIds);
        testRoomId = chatRoom.getRoomId();

        chatRoomService.joinRoom(testRoomId, "user2");
    }

    @Test
    @DisplayName("채팅방 생성 테스트 ")
    void testCreateRoom() throws Exception {

        mockMvc
            .perform(
                post("/api/v1/chat/room") // url
                    .param("name", "테스트 채팅방")
                    .param("memberIds", "user1", "user2")
            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("사용자의 채팅방 목록 조회 테스트 ")
    void testFindMyRooms() throws Exception {
        String memberId = "user1";

        mockMvc
            .perform(
                get("/api/v1/chat/rooms/member-id/"+ memberId) // url
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("채팅방 입장 테스트 ")
    void testJoinRoom() throws Exception {
        mockMvc
            .perform(
                post("/api/v1/chat/room/join/room-id/"+testRoomId) // url
                    .param("memberId", "user1")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("채팅방 퇴장 테스트 ")
    void testLeaveRoom() throws Exception {
        mockMvc
            .perform(
                post("/api/v1/chat/room/leave/room-id/"+testRoomId) // url
                    .param("memberId", "user2")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }
}

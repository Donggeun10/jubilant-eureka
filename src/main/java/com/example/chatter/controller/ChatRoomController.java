package com.example.chatter.controller;


import com.example.chatter.component.DataManipulator;
import com.example.chatter.domain.ChatRoomMemberResponse;
import com.example.chatter.domain.ChatRoomResponse;
import com.example.chatter.entity.ChatRoom;
import com.example.chatter.entity.ChatRoomMember;
import com.example.chatter.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final DataManipulator dataManipulator;

    @Operation(summary = "채팅방 생성", responses = {
        @ApiResponse( responseCode = "201", description = "채팅방 등록 성공" )
    })
    @PostMapping("/room")
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestParam String name, @RequestParam List<String> memberIds) {
        ChatRoom chatRoom = chatRoomService.createRoom(name, memberIds);

        return ResponseEntity.status(HttpStatus.CREATED).body(dataManipulator.makeChatRoomResponse(chatRoom));
    }

    // 사용자 기준 모든 채팅방 목록 반환
    @GetMapping("/rooms/member-id/{memberId}")
    public List<ChatRoomMemberResponse> findMemberRooms(@PathVariable String memberId) {
        List<ChatRoomMember> members = chatRoomService.findAllRoomsByMemberId(memberId);

        return dataManipulator.makeChatRoomMemberResponses(members);
    }

    @PostMapping("/room/join/room-id/{roomId}")
    public void joinRoom(@RequestParam String memberId, @PathVariable String roomId) {

        chatRoomService.joinRoom(roomId, memberId);
    }

    @PostMapping("/room/leave/room-id/{roomId}")
    public void leaveRoom(@RequestParam String memberId, @PathVariable String roomId) {

        chatRoomService.leaveRoom(roomId, memberId);
    }

    @GetMapping("/room/room-id/{roomId}")
    public ChatRoomResponse findRoomInfo(@PathVariable String roomId) {
        ChatRoom chatRoom = chatRoomService.findRoomById(roomId);
        return dataManipulator.makeChatRoomResponse(chatRoom);
    }
}
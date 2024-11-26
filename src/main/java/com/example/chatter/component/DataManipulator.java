package com.example.chatter.component;

import com.example.chatter.domain.ChatRoomMemberResponse;
import com.example.chatter.domain.ChatRoomResponse;
import com.example.chatter.entity.ChatRoom;
import com.example.chatter.entity.ChatRoomMember;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataManipulator {

    public ChatRoomResponse makeChatRoomResponse(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getName())
                .insertedDatetime(chatRoom.getInsertedDatetime())
                .updatedDatetime(chatRoom.getUpdatedDatetime())
                .memberCount(chatRoom.getMemberCount())
                .chatRoomMembers(makeChatRoomMemberResponses(chatRoom, chatRoom.getChatRoomMembers()))
                .build();
    }

    private List<ChatRoomMemberResponse> makeChatRoomMemberResponses(ChatRoom chatRoom, List<ChatRoomMember> chatRoomMembers) {
        List<ChatRoomMemberResponse> chatRoomMemberResponses = new ArrayList<>();
        for(ChatRoomMember chatRoomMember : chatRoomMembers) {
            chatRoomMemberResponses.add(makeChatRoomMemberResponse(chatRoom, chatRoomMember));
        }
        return chatRoomMemberResponses;
    }

    private ChatRoomMemberResponse makeChatRoomMemberResponse(ChatRoom chatRoom, ChatRoomMember chatRoomMember) {
        return ChatRoomMemberResponse.builder()
            .memberId(chatRoomMember.getPk().getAmemberId())
            .statusType(chatRoomMember.getStatusType())
            .updatedDatetime(chatRoomMember.getUpdatedDatetime())
            .insertedDatetime(chatRoomMember.getInsertedDatetime())
            .roomId(chatRoom.getRoomId())
            .build();
    }

    public List<ChatRoomMemberResponse> makeChatRoomMemberResponses(List<ChatRoomMember> chatRoomMembers) {
        List<ChatRoomMemberResponse> chatRoomMemberResponses = new ArrayList<>();
        for(ChatRoomMember chatRoomMember : chatRoomMembers) {
            chatRoomMemberResponses.add(makeChatRoomMemberResponseWithChatRoom(chatRoomMember.getChatRoom(), chatRoomMember));
        }
        return chatRoomMemberResponses;
    }

    private ChatRoomMemberResponse makeChatRoomMemberResponseWithChatRoom(ChatRoom chatRoom, ChatRoomMember chatRoomMember) {
        return ChatRoomMemberResponse.builder()
            .memberId(chatRoomMember.getPk().getAmemberId())
            .statusType(chatRoomMember.getStatusType())
            .updatedDatetime(chatRoomMember.getUpdatedDatetime())
            .insertedDatetime(chatRoomMember.getInsertedDatetime())
            .roomId(chatRoom.getRoomId())
            .chatRoom(makeChatRoomResponseWithoutMember(chatRoom))
            .build();
    }

    private ChatRoomResponse makeChatRoomResponseWithoutMember(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
            .roomId(chatRoom.getRoomId())
            .name(chatRoom.getName())
            .insertedDatetime(chatRoom.getInsertedDatetime())
            .updatedDatetime(chatRoom.getUpdatedDatetime())
            .memberCount(chatRoom.getMemberCount())
            .build();
    }
}

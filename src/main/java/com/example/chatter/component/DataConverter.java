package com.example.chatter.component;

import com.example.chatter.entity.ChatMessage;
import com.example.chatter.entity.ChatMessage.MessageType;
import com.example.chatter.entity.ChatRoomMember;
import com.example.chatter.entity.ChatRoomMember.ChatRoomMemberPk;
import com.example.chatter.service.ChatRoomService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataConverter {

    final ChatRoomService chatRoomService;

    public List<ChatRoomMember> convertMessageToRoomMember(List<ChatMessage> chatMessages)  {

            List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

            for(ChatMessage chatMessage : chatMessages) {

                chatRoomMembers.add(ChatRoomMember.builder()
                                        .chatRoom(chatRoomService.findRoomById(chatMessage.getChatRoomId()))
                                        .pk(ChatRoomMemberPk.builder().amemberId(chatMessage.getSender()).broomId(chatMessage.getChatRoomId()).build())
                                        .statusType(chatMessage.getType().equals(MessageType.ENTER) ? ChatRoomMember.MemberStatusType.ACTIVE : ChatRoomMember.MemberStatusType.INACTIVE)
                                        .build());

            }

            return chatRoomMembers;
    }

}

package com.example.chatter.service;

import com.example.chatter.entity.ChatRoom;
import com.example.chatter.entity.ChatRoomMember;
import com.example.chatter.entity.ChatRoomMember.ChatRoomMemberPk;
import com.example.chatter.entity.ChatRoomMember.MemberStatusType;
import com.example.chatter.exception.NotFoundChatRoomException;
import com.example.chatter.exception.NotFoundChatRoomMemberException;
import com.example.chatter.repository.ChatRoomMemberRepository;
import com.example.chatter.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public ChatRoom createRoom(String name, List<String> memberIds) {
        ChatRoom chatRoom = ChatRoom.create(name);
        List<ChatRoomMember> members = new ArrayList<>();
        for(String memberId : memberIds) {
            ChatRoomMember member =
                ChatRoomMember.builder().chatRoom(chatRoom).pk(ChatRoomMemberPk.builder().amemberId(memberId).broomId(chatRoom.getRoomId()).build()).statusType(MemberStatusType.INACTIVE).build();
            members.add(member);
        }
        chatRoom.setChatRoomMembers(members);

        chatRoomRepository.save(chatRoom);

        return chatRoomRepository.findById(chatRoom.getRoomId()).orElseThrow();
    }

    public void joinRoom(String roomId, String memberId) {
        ChatRoom chatRoom = findRoomById(roomId);
        chatRoom.incrementMemberCount();
        if(chatRoom.getChatRoomMembers().stream().anyMatch(member -> member.getPk().getAmemberId().equals(memberId))) {
            for(ChatRoomMember member : chatRoom.getChatRoomMembers()) {
                if(member.getPk().getAmemberId().equals(memberId)) {
                    member.updateStatusType(MemberStatusType.ACTIVE);
                    break;
                }
            }
        }else{
            ChatRoomMember chatRoomMember = ChatRoomMember.builder().chatRoom(chatRoom).pk(ChatRoomMemberPk.builder().amemberId(memberId).broomId(chatRoom.getRoomId()).build()).statusType(MemberStatusType.ACTIVE).build();
            chatRoom.getChatRoomMembers().add(chatRoomMember);
        }

        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void leaveRoom(String roomId, String memberId) {
        ChatRoom chatRoom = findRoomById(roomId);
        chatRoom.decrementMemberCount();
        List<ChatRoomMember> newMembers = new ArrayList<>();
        for(ChatRoomMember member : chatRoom.getChatRoomMembers()) {
            if(!member.getPk().getAmemberId().equals(memberId)) {
                newMembers.add(member);
            }
        }
        if(chatRoom.getChatRoomMembers().size() == newMembers.size()) {
            throw new NotFoundChatRoomMemberException(String.format("Member not found in chat room(%s). please check memberId:%s", roomId, memberId));
        }
        chatRoom.setChatRoomMembers(newMembers);
        chatRoomRepository.save(chatRoom);
        chatRoomMemberRepository.deleteById(ChatRoomMemberPk.builder().amemberId(memberId).broomId(roomId).build());
    }

    public List<ChatRoomMember> findAllRoomsByMemberId(String memberId) {
        return chatRoomMemberRepository.findByPkAmemberId(memberId);
    }

    public ChatRoom findRoomById(String roomId) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
        if(chatRoomOpt.isPresent()) {
            return chatRoomOpt.get();
        }else {
            throw new NotFoundChatRoomException(String.format("Chat room not found. please check roomId:%s", roomId));
        }
    }
}

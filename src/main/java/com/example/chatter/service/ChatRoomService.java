package com.example.chatter.service;

import com.example.chatter.entity.ChatRoom;
import com.example.chatter.entity.ChatRoomMember;
import com.example.chatter.entity.ChatRoomMember.MemberStatusType;
import com.example.chatter.exception.NotFoundChatRoomException;
import com.example.chatter.repository.ChatRoomMemberRepository;
import com.example.chatter.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Transactional
    public ChatRoom createRoom(String name, List<String> memberIds) {
        ChatRoom chatRoom = ChatRoom.create(name);
        chatRoomRepository.save(chatRoom);

        List<ChatRoomMember> members = new ArrayList<>();
        for(String memberId : memberIds) {
            members.add(ChatRoomMember.builder().chatRoom(chatRoom).memberId(memberId).build());
        }
        chatRoomMemberRepository.saveAll(members);

        return chatRoomRepository.findById(chatRoom.getRoomId()).orElseThrow();
    }

    @Transactional
    public void joinRoom(String roomId, String memberId) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
        if(chatRoomOpt.isPresent()) {
            ChatRoom chatRoom = chatRoomOpt.get();
            chatRoom.incrementMemberCount();
            ChatRoomMember chatRoomMember = ChatRoomMember.builder().chatRoom(chatRoom).memberId(memberId).statusType(MemberStatusType.ACTIVE).build();

            chatRoomRepository.save(chatRoom);
            chatRoomMemberRepository.save(chatRoomMember);
        }else{
            throw new NotFoundChatRoomException(String.format("Chat room not found to join. please check roomId:%s, memberId:%s", roomId, memberId));
        }
    }

    @Transactional
    public void leaveRoom(String roomId, String memberId) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
        if(chatRoomOpt.isPresent()) {
            ChatRoom chatRoom = chatRoomOpt.get();
            chatRoom.decrementMemberCount();
            ChatRoomMember chatRoomMember = ChatRoomMember.builder().chatRoom(chatRoom).memberId(memberId).build();

            chatRoomRepository.save(chatRoom);
            chatRoomMemberRepository.delete(chatRoomMember);
        }else{
            throw new NotFoundChatRoomException(String.format("Chat room not found to leave. please check roomId:%s, memberId:%s", roomId, memberId));
        }
    }

    public List<ChatRoomMember> findAllRoomsByMemberId(String memberId) {
        return chatRoomMemberRepository.findByMemberId(memberId);
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow();
    }
}

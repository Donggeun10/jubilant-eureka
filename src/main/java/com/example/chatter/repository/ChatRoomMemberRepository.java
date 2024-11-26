package com.example.chatter.repository;

import com.example.chatter.entity.ChatRoomMember;
import com.example.chatter.entity.ChatRoomMember.ChatRoomMemberPk;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, ChatRoomMemberPk> {

    List<ChatRoomMember> findByPkAmemberId(String memberId);

}

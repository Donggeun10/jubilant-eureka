package com.example.chatter.repository;

import com.example.chatter.entity.ChatRoomMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, String> {

    List<ChatRoomMember> findByMemberId(String memberId);

}

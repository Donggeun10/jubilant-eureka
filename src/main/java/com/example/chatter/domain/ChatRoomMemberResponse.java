package com.example.chatter.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.example.chatter.entity.ChatRoomMember.MemberStatusType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(NON_NULL)
@Builder
@Getter
public class ChatRoomMemberResponse implements Serializable { // 순환참조로 인한 json 변환 오류 발생으로 Response 클래스 추가

    private String memberId;
    private MemberStatusType statusType;
    private Timestamp insertedDatetime;
    private Timestamp updatedDatetime;

    private String roomId;

    private ChatRoomResponse chatRoom;
}

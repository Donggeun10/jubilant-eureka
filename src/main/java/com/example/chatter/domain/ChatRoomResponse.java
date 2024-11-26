package com.example.chatter.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import lombok.Builder;
import lombok.Getter;


@JsonInclude(NON_NULL)
@Builder
@Getter
public class ChatRoomResponse implements Serializable { // 순환참조로 인한 json 변환 오류 발생으로 Response 클래스 추가

    private String roomId;
    private String name;
    private Timestamp insertedDatetime;
    private Timestamp updatedDatetime;
    private int memberCount;
    private List<ChatRoomMemberResponse> chatRoomMembers;

}

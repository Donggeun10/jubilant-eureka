package com.example.chatter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessage implements Serializable {

    public enum MessageType {
        ENTER, TALK, EXIT, MATCH, MATCH_REQUEST;
    }
    MessageType type;

    Long chatRoomId;
    String sender;
    String message;

    @Id
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());




}

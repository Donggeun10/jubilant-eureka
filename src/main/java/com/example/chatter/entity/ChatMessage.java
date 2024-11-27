package com.example.chatter.entity;

import jakarta.persistence.Column;
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
public class ChatMessage implements Serializable { // it will be considered composed primary key timestamp + chatRoomId

    public enum MessageType {
        ENTER, TALK, EXIT;
    }
    private MessageType type;

    private String chatRoomId;
    private String sender;

    @Column(length = 1000)
    private String message;

    @Id
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

}

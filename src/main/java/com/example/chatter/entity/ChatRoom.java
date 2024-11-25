package com.example.chatter.entity;

import com.example.chatter.exception.ChatRoomFullException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatRoom implements Serializable {

    @Id
    @Column(name = "room_id")
    private String roomId;
    private String name;

    public static ChatRoom create(String name) {
        return ChatRoom.builder()
            .name(name)
            .roomId(UUID.randomUUID().toString())
            .build();

    }

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp insertedDatetime;

    @UpdateTimestamp
    private Timestamp updatedDatetime;

    private int memberCount = 0;

    public void incrementMemberCount() {
        this.memberCount++;
        if(this.memberCount > 100) {
            throw new ChatRoomFullException(String.format("Chat room(%s) is full", roomId));
        }
    }

    public void decrementMemberCount() {
        this.memberCount--;
    }

    @OneToMany
    @Exclude
    private List<ChatRoomMember> chatRoomMembers;

    public void setChatRoomMembers(List<ChatRoomMember> chatRoomMembers) {
        this.chatRoomMembers = chatRoomMembers;
    }


}
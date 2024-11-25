package com.example.chatter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatRoomMember implements Serializable {

    @Id
    private String memberId;

    public enum MemberStatusType {
        ACTIVE, INACTIVE;
    }
    MemberStatusType statusType;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp insertedDatetime;

    @UpdateTimestamp
    private Timestamp updatedDatetime;

    @ManyToOne
    private ChatRoom chatRoom;
}

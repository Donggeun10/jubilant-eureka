package com.example.chatter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMember implements Serializable {

    @EmbeddedId
    private ChatRoomMemberPk pk;

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
    @MapsId("broomId")
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Getter
    @Embeddable
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomMemberPk implements Serializable {
        /**
         *
         */
        @Serial
        private static final long serialVersionUID = 5311620843608365136L;

        @Column(name = "member_id")
        private String amemberId;

        @Column(name = "room_id")
        private String broomId;

    }
}

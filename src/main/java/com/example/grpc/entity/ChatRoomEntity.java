package com.example.grpc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Entity
@Table(name = "chat_room")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private MemberEntity creator;

    @Column(name = "is_private", nullable = false)
    private String isPrivateYN; // 비공개 여부 Y, N

    // factory method
    public static ChatRoomEntity of(String roomName, String isPrivate, MemberEntity creator, LocalDateTime now) {
        return ChatRoomEntity.builder()
                .name(roomName)
                .isPrivateYN(isPrivate)
                .creator(creator)
                .createdAt(now)
                .build();
    }

}

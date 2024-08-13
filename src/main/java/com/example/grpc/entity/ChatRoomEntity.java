package com.example.grpc.entity;

import javax.persistence.*;

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

    // factory method
    public static ChatRoomEntity of(String roomName, MemberEntity creator, LocalDateTime now) {
        return ChatRoomEntity.builder()
                .name(roomName)
                .creator(creator)
                .createdAt(now)
                .build();
    }

}

package com.example.grpc.service.chat;

import com.example.grpc.entity.ChatMessageEntity;
import com.example.grpc.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;


    /**
     * @param roomId 채팅방 ID
     * @return 채팅 내역
     * @apiNote 채팅방의 채팅 내역을 조회하는 메서드
     */
    public List<ChatMessageEntity> findByRoomIdOrderByTimestampAsc(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

}

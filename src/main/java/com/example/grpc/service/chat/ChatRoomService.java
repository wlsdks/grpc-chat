package com.example.grpc.service.chat;

import com.example.grpc.controller.dto.ChatRoomResponseDTO;
import com.example.grpc.controller.dto.CreateRoomRequestDTO;
import com.example.grpc.entity.ChatRoomEntity;
import com.example.grpc.entity.MemberEntity;
import com.example.grpc.repository.ChatRoomRepository;
import com.example.grpc.repository.MemberRepository;
import com.example.grpc.repository.dsl.ChatRoomDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomDslRepository chatRoomDslRepository;


    /**
     * @param requestDTO   채팅방 생성 요청 정보
     * @param creatorEmail 채팅방 생성자 이메일
     * @return 생성된 채팅방 정보
     * @apiNote 채팅방 생성
     */
    public ChatRoomResponseDTO createChatRoom(CreateRoomRequestDTO requestDTO, String creatorEmail) {
        MemberEntity creator = memberRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ChatRoomEntity chatRoom = ChatRoomEntity.of(requestDTO.getRoomName(), requestDTO.getIsPrivateYN(), creator, LocalDateTime.now());

        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomResponseDTO.entityToResponseDto(savedChatRoom);
    }


    /**
     * @return 채팅방 목록
     * @apiNote 채팅방 목록 조회
     */
    public List<ChatRoomResponseDTO> findAllChatRooms() {
        // 비공개 채팅방은 제외 (ture면 비공개가 아닌 채팅방 조회, false면 비공개 채팅방 조회)
        List<ChatRoomEntity> chatRoomEntityList = chatRoomDslRepository.findAllChatRoomsNotPrivate();
        return chatRoomEntityList.stream()
                .map(ChatRoomResponseDTO::entityToResponseDto)
                .toList();
    }

}

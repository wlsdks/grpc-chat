package com.example.grpc.client.chat;

import com.example.chat.ChatMessage;
import com.example.chat.ChatServiceGrpc;
import com.example.grpc.entity.ChatMessageEntity;
import com.example.grpc.entity.MemberEntity;
import com.example.grpc.repository.ChatMessageRepository;
import com.example.grpc.repository.MemberRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grpc 채팅 클라이언트
 */
@RequiredArgsConstructor
@Service
public class GrpcChatClient {

    @GrpcClient("chat-server")
    private ChatServiceGrpc.ChatServiceStub chatServiceStub;

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();


    /**
     * @param roomId  채팅방 ID
     * @apiNote 클라이언트가 새로고침으로 인해 연결이 끊어질 때, 서버는 해당 클라이언트에 대한 SSE 연결을 정리해야 합니다.
     * 이를 위해 SseEmitter에 대해 onCompletion 및 onTimeout 콜백을 설정해 연결이 끊어졌을 때 해당 SseEmitter를 제거해야 합니다.
     */
    public SseEmitter addEmitter(String roomId) {
        // 새로운 SseEmitter 생성 (5분 타임아웃 설정)
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        // roomId에 해당하는 채팅방의 emitters 리스트를 가져와서 emitter를 추가한다.
        emitters.computeIfAbsent(roomId, k -> new ArrayList<>()).add(emitter);

        // 타임아웃 및 완료 핸들러 설정
        emitter.onCompletion(() -> removeEmitter(roomId, emitter));
        emitter.onTimeout(() -> {
            System.out.println("Emitter timed out for roomId: " + roomId);
            removeEmitter(roomId, emitter);
        });

//        try {
//            emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
//        } catch (IOException e) {
//            removeEmitter(roomId, emitter);
//            // 추가된 로깅: 문제가 발생한 Emitter를 식별하기 위해 로깅
//            System.out.println("Failed to send initial keep-alive message to emitter for roomId: " + roomId);
//        }

        return emitter;
    }


    /**
     * @apiNote 클라이언트에게 주기적으로 ping 메시지를 전송하는 메서드
     * 실제로 클라이언트와의 연결을 유지하기 위해서는 주기적으로(예: 30초마다) 이와 같은 "ping" 메시지를 보내야 하며,
     * 이를 위해서는 서버 측에서 스케줄러를 사용하여 주기적으로 SseEmitter에 메시지를 전송하는 로직이 필요합니다.
     * @EnableScheduling 어노테이션을 사용하여 스케줄러를 활성화하고, @Scheduled 어노테이션을 사용하여 주기적으로 메서드를 실행할 수 있습니다.
     *
     * 주기적으로 "ping" 메시지를 보내는 로직은 접속한 모든 클라이언트에 대해 개별적으로 실행됩니다. 각 클라이언트가 서버에 연결할 때마다 서버는 해당 클라이언트에 대해 별도의 SseEmitter를 생성하며, 이 SseEmitter는 특정 채팅방(roomId)에 속한 모든 클라이언트에게 메시지를 보내는 역할을 합니다.
     * @Scheduled로 구현된 sendPingMessages 메서드는 모든 클라이언트의 SseEmitter 목록을 순회하며, 각 SseEmitter에 대해 "ping" 메시지를 주기적으로 전송합니다. 따라서 채팅방에 여러 명의 클라이언트가 접속해 있다면, 이 메서드는 그 채팅방에 연결된 모든 클라이언트에게 개별적으로 "ping" 메시지를 보냅니다.
     */
    @Scheduled(fixedRate = 30000)  // 30초마다 실행
    public void sendPingMessages() {
        emitters.forEach((roomId, emitterList) -> {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for (SseEmitter emitter : emitterList) {
                try {
                    emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                    System.out.println("Failed to send ping message to emitter for roomId: " + roomId);
                }
            }
            emitterList.removeAll(deadEmitters);
        });
    }


    /**
     * @param roomId  채팅방 ID
     * @param emitter sseEmitter
     * @apiNote sseEmitter를 제거하는 메서드
     */
    private void removeEmitter(String roomId, SseEmitter emitter) {
        List<SseEmitter> roomEmitters = emitters.get(roomId);
        if (roomEmitters != null) {
            roomEmitters.remove(emitter);
        }
    }


    /**
     * @param roomId    채팅방 ID
     * @param userEmail 사용자 이메일
     * @param message   메시지
     * @apiNote 채팅 메시지를 전송하는 메서드
     */
    public void sendMessage(String roomId, String userEmail, String message) {
        // 사용자 이메일로 사용자 조회
        MemberEntity memberEntity = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + userEmail));

        // ChatMessage 생성
        ChatMessage chatMessage = ChatMessage.newBuilder()
                .setUser(memberEntity.getName())
                .setMessage(message)
                .build();

        // gRPC를 통해 메시지를 서버로 전송 (chatServiceStub.chat 메서드를 사용하여 gRPC 스트리밍을 시작합니다.)
        // StreamObserver<ChatMessage>를 통해 gRPC 서버로 메시지를 전송하고, 서버에서 수신된 메시지를 처리합니다.
        StreamObserver<ChatMessage> requestObserver = chatServiceStub.chat(new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage value) {
                // gRPC로부터 메시지를 수신했을 때의 로직 (서버에서 메시지를 수신했을 때 호출되며, 이 메시지를 채팅방의 클라이언트에게 브로드캐스트합니다.)
                broadcastToClients(roomId, value);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("gRPC error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("gRPC communication completed.");
            }


        });

        // 메시지를 gRPC 서버로 전송
        requestObserver.onNext(chatMessage);

        // 필요에 따라 스트림을 완료합니다.
        // requestObserver.onCompleted();

        // ChatMessageEntity 생성
        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .roomId(roomId)
                .memberEntity(memberEntity)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        // 메시지 저장
        chatMessageRepository.save(chatMessageEntity);
    }


    /**
     * @param roomId  채팅방 ID
     * @param message 채팅 메시지
     * @apiNote 클라이언트에게 메시지를 브로드캐스트하는 메서드
     */
    private void broadcastToClients(String roomId, ChatMessage message) {
        // 1. roomId에 해당하는 채팅방의 emitters를 가져온다.
        List<SseEmitter> roomEmitters = emitters.get(roomId);

        // 2. emitters가 null이 아니라면, emitters에 있는 모든 emitter에게 메시지를 전송한다.
        if (roomEmitters != null) {
            // deadEmitters 리스트를 생성한다. (deadEmitters: 메시지 전송에 실패한 emitter를 저장하는 리스트)
            List<SseEmitter> deadEmitters = new ArrayList<>();

            // roomEmitters에 있는 모든 emitter에게 메시지를 전송한다.
            for (SseEmitter emitter : roomEmitters) {
                try {
                    emitter.send(SseEmitter.event().data(message.getUser() + ": " + message.getMessage()));
                } catch (Exception e) {
                    deadEmitters.add(emitter);
                }
            }

            // 문제 있는 Emitter들을 roomEmitters에서 제거
            for (SseEmitter deadEmitter : deadEmitters) {
                removeEmitter(roomId, deadEmitter);
            }
        }

    }

}

package com.example.grpc.client.chat;

import com.example.chat.ChatMessage;
import com.example.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grpc 채팅 클라이언트
 */
@Service
public class GrpcChatClient {

    @GrpcClient("chat-server")
    private ChatServiceGrpc.ChatServiceStub chatServiceStub;

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();


    /**
     * @param user user
     * @apiNote 채팅을 시작하는 메서드
     */
    public void startChat(String roomId, String user) {
        StreamObserver<ChatMessage> requestObserver = chatServiceStub.chat(new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage value) {
                broadcastToClients(roomId, value);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Chat ended.");
            }
        });

        // 초기 메시지로 채팅방 ID를 전송
        requestObserver.onNext(ChatMessage.newBuilder().setMessage(roomId).build());
    }

    /**
     * @param roomId  채팅방 ID
     * @param emitter sseEmitter
     * @apiNote sseEmitter를 추가하는 메서드
     */
    public void addEmitter(String roomId, SseEmitter emitter) {
        emitters.computeIfAbsent(roomId, k -> new ArrayList<>()).add(emitter);
    }


    /**
     * @param roomId  채팅방 ID
     * @param user    user
     * @param message message
     * @apiNote 메시지를 전송하는 메서드
     */
    public void sendMessage(String roomId, String user, String message) {
        ChatMessage chatMessage = ChatMessage.newBuilder()
                .setUser(user)
                .setMessage(message)
                .build();

        broadcastToClients(roomId, chatMessage);
    }


    /**
     * @param roomId  채팅방 ID
     * @param message 채팅 메시지
     * @apiNote 클라이언트에게 메시지를 브로드캐스트하는 메서드
     */
    private void broadcastToClients(String roomId, ChatMessage message) {
        List<SseEmitter> roomEmitters = emitters.get(roomId);
        if (roomEmitters != null) {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for (SseEmitter emitter : roomEmitters) {
                try {
                    emitter.send(SseEmitter.event().data(message.getUser() + ": " + message.getMessage()));
                } catch (Exception e) {
                    deadEmitters.add(emitter);
                }
            }
            roomEmitters.removeAll(deadEmitters);
        }
    }


}

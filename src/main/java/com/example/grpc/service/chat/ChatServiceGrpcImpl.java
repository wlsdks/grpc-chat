package com.example.grpc.service.chat;


import com.example.chat.ChatMessage;
import com.example.chat.ChatServiceGrpc;
import com.example.grpc.controller.chat.ChatRoom;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅 Grpc 서비스 구현체
 */
@GrpcService
public class ChatServiceGrpcImpl extends ChatServiceGrpc.ChatServiceImplBase {

    private Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    /**
     * @param responseObserver 클라이언트로부터 전달받은 메시지를 응답하는 스트림
     * @return 클라이언트로부터 전달받은 메시지를 응답하는 스트림
     * @apiNote 채팅 메시지를 주고받는 메서드
     */
    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        // 초기에는 채팅방 ID를 요구하도록 한다.
        return new StreamObserver<ChatMessage>() {
            // 클라이언트로부터 메시지를 전달받으면 모든 클라이언트에게 전달한다.
            private String currentRoomId;

            @Override
            public void onNext(ChatMessage chatMessage) {
                if (currentRoomId == null) {
                    currentRoomId = chatMessage.getMessage(); // 첫 메시지를 채팅방 ID로 사용
                    ChatRoom chatRoom = chatRooms.computeIfAbsent(currentRoomId, ChatRoom::new); // 채팅방이 없으면 생성
                    chatRoom.join(responseObserver);
                } else {
                    ChatRoom chatRoom = chatRooms.get(currentRoomId);
                    if (chatRoom != null) {
                        chatRoom.broadcast(chatMessage);
                    }
                }
            }

            // 클라이언트로부터 에러가 발생하면 해당 클라이언트를 제거한다.
            @Override
            public void onError(Throwable throwable) {
                ChatRoom chatRoom = chatRooms.get(currentRoomId);
                if (chatRoom != null) {
                    chatRoom.leave(responseObserver);
                }
            }

            // 클라이언트로부터 완료 신호를 받으면 해당 클라이언트를 제거한다.
            @Override
            public void onCompleted() {
                ChatRoom chatRoom = chatRooms.get(currentRoomId);
                if (chatRoom != null) {
                    chatRoom.leave(responseObserver);
                }
                responseObserver.onCompleted();
            }
        };
    }

}

package com.example.grpc.service.chat;


import com.example.chat.ChatMessage;
import com.example.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

/**
 * 채팅 Grpc 서비스 구현체
 */
@GrpcService
public class ChatServiceGrpcImpl extends ChatServiceGrpc.ChatServiceImplBase {

    private List<StreamObserver<ChatMessage>> observers = new ArrayList<>();

    /**
     * @param responseObserver 클라이언트로부터 전달받은 메시지를 응답하는 스트림
     * @return 클라이언트로부터 전달받은 메시지를 응답하는 스트림
     * @apiNote 채팅 메시지를 주고받는 메서드
     */
    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        // 클라이언트로부터 전달받은 메시지를 응답하는 스트림을 observers 리스트에 추가한다.
        observers.add(responseObserver);

        return new StreamObserver<ChatMessage>() {
            // 클라이언트로부터 메시지를 전달받으면 모든 클라이언트에게 전달한다.
            @Override
            public void onNext(ChatMessage chatMessage) {
                for (StreamObserver<ChatMessage> observer : observers) {
                    observer.onNext(chatMessage);
                }
            }

            // 클라이언트로부터 에러가 발생하면 해당 클라이언트를 제거한다.
            @Override
            public void onError(Throwable throwable) {
                observers.remove(responseObserver);
            }

            // 클라이언트로부터 완료 신호를 받으면 해당 클라이언트를 제거한다.
            @Override
            public void onCompleted() {
                observers.remove(responseObserver);
                responseObserver.onCompleted();
            }
        };
    }

}

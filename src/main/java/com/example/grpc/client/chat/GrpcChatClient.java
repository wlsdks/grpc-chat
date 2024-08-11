package com.example.grpc.client.chat;

import com.example.chat.ChatMessage;
import com.example.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * Grpc 채팅 클라이언트
 */
@Service
public class GrpcChatClient {

    @GrpcClient("chat-server")
    private ChatServiceGrpc.ChatServiceStub chatServiceStub;

    /**
     * @apiNote 채팅을 시작하는 메서드
     * @param user user
     * @param message message
     */
    public void startChat(String user, String message) {
        StreamObserver<ChatMessage> requestObserver = chatServiceStub.chat(new StreamObserver<ChatMessage>() {
            // 서버로부터 메시지를 전달받으면 콘솔에 출력한다.
            @Override
            public void onNext(ChatMessage value) {
                System.out.println(value.getUser() + ": " + value.getMessage());
            }

            // 서버로부터 에러가 발생하면 콘솔에 출력한다.
            @Override
            public void onError(Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }

            // 서버로부터 완료 신호를 받으면 콘솔에 출력한다.
            @Override
            public void onCompleted() {
                System.out.println("Chat completed");
            }
        });

        // 클라이언트로부터 전달받은 메시지를 서버로 전송한다.
        requestObserver.onNext(ChatMessage.newBuilder().setUser(user).setMessage(message).build());
    }

}

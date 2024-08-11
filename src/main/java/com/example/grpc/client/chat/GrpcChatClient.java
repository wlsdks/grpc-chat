package com.example.grpc.client.chat;

import com.example.chat.ChatMessage;
import com.example.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * Grpc 채팅 클라이언트
 */
@Service
public class GrpcChatClient {

    @GrpcClient("chat-server")
    private ChatServiceGrpc.ChatServiceStub chatServiceStub;

    /**
     * @param user user
     * @apiNote 채팅을 시작하는 메서드
     */
    public void startChat(String roomId, String user) {
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
                System.out.println("Chat ended.");
            }
        });

        // 첫 메시지로 채팅방 ID를 전송
        requestObserver.onNext(ChatMessage.newBuilder().setMessage(roomId).build());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("메시지를 입력하세요: ");
            String inputMessage = scanner.nextLine();
            if ("종료".equalsIgnoreCase(inputMessage)) {
                break; // 종료 입력시 채팅 종료
            }

            // 사용자 메시지 전송
            requestObserver.onNext(ChatMessage.newBuilder()
                    .setUser(user)
                    .setMessage(inputMessage)  // 메시지 필드를 설정합니다.
                    .build());
        }

        // 채팅 종료
        requestObserver.onCompleted();
    }

}

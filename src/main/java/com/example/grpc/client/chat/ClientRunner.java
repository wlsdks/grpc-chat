package com.example.grpc.client.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 클라이언트 실행 (gRPC)
 */
@RequiredArgsConstructor
@Component
public class ClientRunner implements ApplicationRunner {

    private final GrpcChatClient grpcChatClient;

    /**
     * @param args ApplicationArguments
     * @throws Exception 예외
     * @apiNote 클라이언트 실행 메서드
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 첫 번째 클라이언트 실행
        new Thread(() -> {
            try {
                grpcChatClient.startChat("room1", "User1", "Hello from User1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 두 번째 클라이언트 실행
        new Thread(() -> {
            try {
                grpcChatClient.startChat("room1", "User2", "Hello from User2");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}

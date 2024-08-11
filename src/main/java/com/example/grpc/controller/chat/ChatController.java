package com.example.grpc.controller.chat;

import com.example.grpc.client.chat.GrpcChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final GrpcChatClient grpcChatClient;


    /**
     * @param user    user
     * @param message message
     * @return ResponseEntity
     * @apiNote 채팅 메시지를 전송하는 메서드
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(
            @RequestParam String user,
            @RequestParam String message
    ) {
        grpcChatClient.startChat(user, message);
        return ResponseEntity.ok().build();
    }

}

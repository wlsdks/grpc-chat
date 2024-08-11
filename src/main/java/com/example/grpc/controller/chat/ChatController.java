package com.example.grpc.controller.chat;

import com.example.grpc.client.chat.GrpcChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/send/{roomId}")
    public ResponseEntity<Void> sendMessage(
            @PathVariable String roomId,
            @RequestParam String user,
            @RequestParam String message
    ) {
        grpcChatClient.startChat(roomId, user, message);
        return ResponseEntity.ok().build();
    }

}

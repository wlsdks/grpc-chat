package com.example.grpc.controller.chat;

import com.example.grpc.client.chat.GrpcChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Grpc Chat Controller
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final GrpcChatClient grpcChatClient;

    // http://localhost:8090/chat/{roomId}?user={user} 에 접속하면 chat.mustache 템플릿을 렌더링합니다.
    // Mustache 템플릿을 렌더링하는 메서드
    @GetMapping("/{roomId}")
    public String chatRoom(
            @PathVariable String roomId,
            @RequestParam String user,
            Model model
    ) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("user", user);
        return "chat";
    }


    // SSE로 클라이언트에게 메시지를 스트리밍하는 엔드포인트
    @GetMapping(value = "/stream/{roomId}", produces = "text/event-stream")
    public SseEmitter streamChat(@PathVariable String roomId) {
        SseEmitter emitter = new SseEmitter();
        grpcChatClient.addEmitter(roomId, emitter);
        return emitter;
    }


    // 메시지를 전송하는 엔드포인트
    @PostMapping("/send/{roomId}")
    public ResponseEntity<Void> sendMessage(
            @PathVariable String roomId,
            @RequestParam String user,
            @RequestParam String message
    ) {
        System.out.println("Received message from " + user + ": " + message);
        grpcChatClient.sendMessage(roomId, user, message);
        return ResponseEntity.ok().build();
    }

}
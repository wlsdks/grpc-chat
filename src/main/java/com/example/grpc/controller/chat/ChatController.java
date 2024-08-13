package com.example.grpc.controller.chat;

import com.example.grpc.client.chat.GrpcChatClient;
import com.example.grpc.controller.dto.MemberDTO;
import com.example.grpc.entity.MemberEntity;
import jakarta.servlet.http.HttpSession;
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


    /**
     * @param roomId  채팅방 ID
     * @param session 세션
     * @param model   모델
     * @return chat.mustache 템플릿
     * @apiNote http://localhost:8090/chat/{roomId}?user={user} 에 접속하면 chat.mustache 템플릿을 렌더링합니다.
     */
    @GetMapping("/{roomId}")
    public String chatRoom(
            @PathVariable String roomId,
            HttpSession session,
            Model model
    ) {
        // 세션에 user가 없으면 로그인 페이지로 리다이렉트합니다.
        MemberDTO member = (MemberDTO) session.getAttribute("user");
        if (member == null) {
            return "redirect:/login";
        }

        // roomId와 user를 모델에 추가하여 chat.mustache 템플릿에 전달합니다.
        model.addAttribute("roomId", roomId);
        model.addAttribute("user", member.getName());

        // chat.mustache 템플릿을 렌더링합니다.
        return "chat";
    }


    /**
     * @param roomId 채팅방 ID
     * @return SseEmitter
     * @apiNote http://localhost:8090/chat/stream/{roomId} 에 접속하면 채팅 메시지를 스트리밍합니다.
     */
    @GetMapping(value = "/stream/{roomId}", produces = "text/event-stream")
    public SseEmitter streamChat(@PathVariable String roomId) {
        SseEmitter emitter = new SseEmitter();
        grpcChatClient.addEmitter(roomId, emitter);
        return emitter;
    }


    /**
     * @param roomId  채팅방 ID
     * @param user    사용자 이름
     * @param message 메시지
     * @return ResponseEntity<Void>
     * @apiNote http://localhost:8090/chat/send/{roomId} 에 POST 요청을 보내면 채팅 메시지를 전송합니다.
     */
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
package com.example.grpc.controller.chat;

import com.example.grpc.controller.dto.ChatRoomResponseDTO;
import com.example.grpc.controller.dto.MemberDTO;
import com.example.grpc.service.chat.ChatRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/chat")
@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/rooms")
    public String getRooms(Model model, HttpSession session) {
        List<ChatRoomResponseDTO> chatRooms = chatRoomService.findAllChatRooms();
        MemberDTO member = (MemberDTO) session.getAttribute("user");
        if (member != null) {
            model.addAttribute("user", member.getName());
        }
        model.addAttribute("rooms", chatRooms);
        return "rooms";
    }

    @PostMapping("/rooms")
    public String createRoom(@RequestParam String roomName, HttpSession session) {
        MemberDTO member = (MemberDTO) session.getAttribute("user");
        if (member != null) {
            chatRoomService.createChatRoom(roomName, member.getEmail());
        }
        return "redirect:/chat/rooms";
    }

}

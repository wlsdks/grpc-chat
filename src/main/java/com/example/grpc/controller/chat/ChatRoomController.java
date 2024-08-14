package com.example.grpc.controller.chat;

import com.example.grpc.controller.dto.ChatRoomResponseDTO;
import com.example.grpc.controller.dto.CreateRoomRequestDTO;
import com.example.grpc.controller.dto.MemberDTO;
import com.example.grpc.service.chat.ChatRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/chat")
@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/get-rooms")
    public String getRooms(Model model, HttpSession session) {
        List<ChatRoomResponseDTO> chatRooms = chatRoomService.findAllChatRooms();
        MemberDTO member = (MemberDTO) session.getAttribute("user");

        if (member != null) {
            model.addAttribute("user", member.getName());
        }

        model.addAttribute("rooms", chatRooms);
        return "rooms";
    }

    @PostMapping("/create-room")
    public String createRoom(
            @ModelAttribute CreateRoomRequestDTO requestDTO,
            HttpSession session) {
        MemberDTO member = (MemberDTO) session.getAttribute("user");
        if (member != null) {
            chatRoomService.createChatRoom(requestDTO, member.getEmail());
        }
        return "redirect:/chat/get-rooms";
    }


}

package com.example.grpc.controller;

import com.example.grpc.entity.MemberEntity;
import com.example.grpc.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class AuthController {

    private final MemberService memberService;


    /**
     * @return 로그인 페이지
     * @apiNote 로그인 페이지를 렌더링하는 메서드
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    /**
     * @param email    이메일
     * @param password 비밀번호
     * @param session  세션
     * @param model    모델
     * @return 채팅 페이지
     * @apiNote 로그인 요청을 처리하는 메서드
     */
    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        try {
            MemberEntity member = memberService.loginUser(email, password);
            session.setAttribute("user", member);
            return "redirect:/chat";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
    }


    /**
     * @return 회원가입 페이지
     * @apiNote 회원가입 페이지를 렌더링하는 메서드
     */
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }


    /**
     * @param email    이메일
     * @param password 비밀번호
     * @param name     이름
     * @param model    모델
     * @return 로그인 페이지
     * @apiNote 회원가입 요청을 처리하는 메서드
     */
    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password, @RequestParam String name, Model model) {
        try {
            memberService.registerUser(email, password, name);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during registration.");
            return "register";
        }
    }


    /**
     * @param session 세션
     * @return 로그인 페이지
     * @apiNote 로그아웃 요청을 처리하는 메서드
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}

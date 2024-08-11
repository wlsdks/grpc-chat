package com.example.grpc.service;

import com.example.grpc.dto.MemberSignUpRequestDTO;
import com.example.grpc.entity.MemberEntity;
import com.example.grpc.mapper.MemberMapper;
import com.example.grpc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;


    /**
     * @param memberDTO 회원가입 요청 DTO
     * @return 회원 엔티티
     * @apiNote 회원가입을 처리하는 메서드
     */
    public MemberEntity createMember(MemberSignUpRequestDTO memberDTO) {
        MemberEntity memberEntity = memberMapper.dtoToEntity(memberDTO);
        return memberRepository.save(memberEntity);
    }


    /**
     * @param email    이메일
     * @param password 비밀번호
     * @param name     이름
     * @apiNote 이메일로 회원을 조회하는 메서드
     */
    public void registerUser(String email, String password, String name) {
        MemberEntity member = MemberEntity.builder()
                .email(email)
                .password(password) // 비밀번호는 암호화되지 않은 평문으로 저장됩니다. 실제 서비스에서는 암호화가 필요합니다.
                .name(name)
                .build();

        memberRepository.save(member);
    }


    /**
     * @param email    이메일
     * @param password 비밀번호
     * @return 회원 엔티티
     * @apiNote 이메일과 비밀번호로 로그인하는 메서드
     */
    public MemberEntity loginUser(String email, String password) {
        return memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }

}

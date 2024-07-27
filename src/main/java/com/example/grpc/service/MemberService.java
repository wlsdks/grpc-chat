package com.example.grpc.service;

import com.example.grpc.dto.MemberSignUpRequestDTO;
import com.example.grpc.entity.Member;
import com.example.grpc.mapper.MemberMapper;
import com.example.grpc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public Member createMember(MemberSignUpRequestDTO memberDTO) {
        Member member = memberMapper.dtoToEntity(memberDTO);
        return memberRepository.save(member);
    }

}

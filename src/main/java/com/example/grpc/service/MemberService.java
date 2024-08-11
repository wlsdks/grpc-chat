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

    public MemberEntity createMember(MemberSignUpRequestDTO memberDTO) {
        MemberEntity memberEntity = memberMapper.dtoToEntity(memberDTO);
        return memberRepository.save(memberEntity);
    }

}

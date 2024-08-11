package com.example.grpc.client;

import com.test.member.grpc.MemberProto;
import com.test.member.grpc.MemberServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/**
 * gRPC 클라이언트
 * GrpcClient 클래스는 gRPC 클라이언트를 구현한 것으로, 다른 서버 또는 같은 서버 내에서 gRPC 서버 메서드를 호출하는 데 사용됩니다.
 * 이 클래스는 애플리케이션 내에서 gRPC 서버에 요청을 보내는 역할을 합니다.
 */
@Component
public class GrpcMemberClient {

    @GrpcClient("chat-server")
    private MemberServiceGrpc.MemberServiceBlockingStub blockingStub;


    /**
     * @param request 회원 생성 요청
     * @return 회원 생성 응답
     * @apiNote 회원 생성
     */
    public MemberProto.MemberCreateResponse createMember(MemberProto.MemberRequest request) {
        return blockingStub.createMember(request);
    }

}
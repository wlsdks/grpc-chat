package com.example.grpc.config;

import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC 서버 설정 - 인터셉터를 등록하는 클래스
 */
@Configuration
public class GrpcConfig {

    /**
     * gRPC 예외 처리 인터셉터 설정
     */
    @GrpcGlobalServerInterceptor
    ExceptionHandlingInterceptor exceptionHandlingInterceptor() {
        return new ExceptionHandlingInterceptor();
    }

}
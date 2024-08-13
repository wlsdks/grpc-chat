package com.example.grpc.controller.dto;

import lombok.Data;

@Data
public class MemberSignUpRequestDTO {

    private Long id;
    private String email;
    private String password;
    private String name;

}

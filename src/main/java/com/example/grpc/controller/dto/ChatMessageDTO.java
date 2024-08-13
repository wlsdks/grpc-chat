package com.example.grpc.controller.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ChatMessageDTO {

    private String userName;
    private String message;
    private LocalDateTime timestamp;

}

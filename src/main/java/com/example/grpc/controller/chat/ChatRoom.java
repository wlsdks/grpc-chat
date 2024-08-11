package com.example.grpc.controller.chat;

import com.example.chat.ChatMessage;
import io.grpc.stub.StreamObserver;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방
 */
@Getter
public class ChatRoom {

    private String roomId;
    private List<StreamObserver<ChatMessage>> participants = new ArrayList<>();


    /**
     * @param chatRoomId 채팅방 ID
     * @apiNote 채팅방 생성자
     */
    public ChatRoom(String chatRoomId) {
        this.roomId = chatRoomId;
    }


    /**
     * @param participant 참가자
     * @apiNote 채팅방에 참가자를 추가하는 메서드
     */
    public void join(StreamObserver<ChatMessage> participant) {
        participants.add(participant);
    }


    /**
     * @param participant 참가자
     * @apiNote 채팅방에서 참가자를 제거하는 메서드
     */
    public void leave(StreamObserver<ChatMessage> participant) {
        participants.remove(participant);
    }


    /**
     * @param message 채팅 메시지
     * @apiNote 채팅 메시지를 모든 참가자에게 전송하는 메서드
     */
    public void broadcast(ChatMessage message) {
        // 삭제 대상 참가자 리스트 선언
        List<StreamObserver<ChatMessage>> toRemove = new ArrayList<>();

        // 모든 참가자에게 메시지를 전송합니다.
        for (StreamObserver<ChatMessage> participant : participants) {
            try {
                // onNext 메서드를 호출하여 메시지를 전송합니다.
                participant.onNext(message);
            } catch (Exception e) {
                // 만약 participant가 종료되었거나 에러 상태이면 리스트에서 제거합니다.
                toRemove.add(participant);
                System.out.println("Error sending message to participant: " + e.getMessage());
            }
        }
        // 삭제 대상 참가자를 제거합니다.
        participants.removeAll(toRemove);
    }

}

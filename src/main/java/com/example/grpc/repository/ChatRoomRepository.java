package com.example.grpc.repository;

import com.example.grpc.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

}

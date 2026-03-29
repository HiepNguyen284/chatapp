package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatRoomPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomPinRepository extends JpaRepository<ChatRoomPin, Long> {

    @Query("""
        select pin.chatRoom.id
        from ChatRoomPin pin
        where pin.user.id = :userId
    """)
    List<Long> findPinnedRoomIdsByUserId(long userId);

    boolean existsByUser_IdAndChatRoom_Id(long userId, long roomId);

    void deleteByUser_IdAndChatRoom_Id(long userId, long roomId);

    void deleteByChatRoom_Id(long roomId);
}

package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatRoomReadState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomReadStateRepository extends JpaRepository<ChatRoomReadState, Long> {

    Optional<ChatRoomReadState> findByRoomIdAndReaderId(long roomId, long readerId);

    @Query("""
        select state
        from ChatRoomReadState state
        left join fetch state.reader reader
        left join fetch reader.avatar
        where state.room.id = ?1
    """)
    List<ChatRoomReadState> findWithReaderByRoomId(long roomId);
}

package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    @Query("""
        select cm
        from ChatRoomMember cm
        where cm.chatRoom.id = :roomId and cm.user.id = :userId
    """)
    Optional<ChatRoomMember> findByRoomIdAndUserId(long roomId, long userId);

    @Query("""
        select cm
        from ChatRoomMember cm
        where cm.chatRoom.id = :roomId
    """)
    List<ChatRoomMember> findByRoomId(long roomId);

    @Query("""
        select cm.isAdmin = true
        from ChatRoomMember cm
        where cm.chatRoom.id = :roomId and cm.user.id = :userId
    """)
    boolean isUserAdmin(long roomId, long userId);

    @Query("""
        select case when count(cm) > 0 then true else false end
        from ChatRoomMember cm
        where cm.chatRoom.id = :roomId and cm.user.id = :userId
    """)
    boolean isUserMember(long roomId, long userId);

    void deleteByChatRoomIdAndUserId(long roomId, long userId);
}

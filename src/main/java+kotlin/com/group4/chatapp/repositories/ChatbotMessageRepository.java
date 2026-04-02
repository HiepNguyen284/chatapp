package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatbotMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatbotMessageRepository extends JpaRepository<ChatbotMessage, Long> {

    List<ChatbotMessage> findByConversation_IdOrderByCreatedOnAscIdAsc(long conversationId);

    void deleteByConversation_Id(long conversationId);

    Optional<ChatbotMessage> findFirstByConversation_IdOrderByCreatedOnDescIdDesc(long conversationId);

    @Query("""
        select m from ChatbotMessage m
        where m.conversation.id = :conversationId
        order by m.createdOn desc, m.id desc
    """)
    List<ChatbotMessage> findRecentByConversationId(
        @Param("conversationId") long conversationId,
        Pageable pageable
    );
}

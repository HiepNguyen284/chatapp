package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatbotConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatbotConversationRepository extends JpaRepository<ChatbotConversation, Long> {

    List<ChatbotConversation> findByOwner_IdOrderByUpdatedOnDescCreatedOnDesc(long ownerId);

    Optional<ChatbotConversation> findByIdAndOwner_Id(long id, long ownerId);
}

package com.example.demo.conversation.repository;

import com.example.demo.conversation.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepo extends JpaRepository<Conversation, UUID> {
    Page<Conversation> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<Conversation> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

}

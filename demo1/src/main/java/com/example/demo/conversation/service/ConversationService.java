package com.example.demo.conversation.service;

import com.example.demo.conversation.dto.ConversationResponseDTO;
import com.example.demo.conversation.entity.Conversation;
import com.example.demo.conversation.repository.ConversationRepo;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class ConversationService {

    private final ConversationRepo conversationRepo;
    private final UserRepo userRepo;

    public ConversationService(ConversationRepo conversationRepo, UserRepo userRepo) {
        this.conversationRepo = conversationRepo;
        this.userRepo = userRepo;
    }

    public Page<ConversationResponseDTO> getConversationsByUserId(UUID userId, Pageable pageable) {
        // ConversationResponseDto
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User information is required");
        }
        Page<Conversation> conversations = conversationRepo
                .findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable);

        return conversations.map(ConversationResponseDTO::fromEntity);
    }

    public ConversationResponseDTO getConversationById(UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepo.findByIdAndUserIdAndDeletedAtIsNull(conversationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        return ConversationResponseDTO.fromEntity(conversation);
    }

    @Transactional
    public ConversationResponseDTO createConversation(UUID userId) {
        User userProxy = userRepo.getReferenceById(userId);
        Conversation conversation = new Conversation();
        conversation.setUser(userProxy);
        conversation.setTitle("New Chat");

        return ConversationResponseDTO.fromEntity(conversationRepo.save(conversation));
    }

    @Transactional
    public void deleteConversationTemporarily(UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepo.findByIdAndUserIdAndDeletedAtIsNull(conversationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        conversation.setDeletedAt(LocalDateTime.now());
        conversationRepo.save(conversation);
    }

    @Transactional
    public void renameConversation(String title, UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepo.findByIdAndUserIdAndDeletedAtIsNull(conversationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        conversation.setTitle(title);
        conversationRepo.save(conversation);
    }

}

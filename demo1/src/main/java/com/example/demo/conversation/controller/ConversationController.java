package com.example.demo.conversation.controller;

import com.example.demo.conversation.dto.ConversationResponseDTO;
import com.example.demo.conversation.service.ConversationService;
import com.example.demo.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/me")
    public ResponseEntity<Page<ConversationResponseDTO>> getConversationForCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        Page<ConversationResponseDTO> conversations = conversationService
                .getConversationsByUserId(userDetails.getUser().getId(), pageable);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponseDTO> getConversationById(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ConversationResponseDTO conversation = conversationService.getConversationById(id,
                userDetails.getUser().getId());
        return ResponseEntity.ok(conversation);

    }

    @PostMapping()
    public ResponseEntity<ConversationResponseDTO> createConversation(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ConversationResponseDTO conversation = conversationService
                .createConversation(userDetails.getUser().getId());
        return ResponseEntity.ok(conversation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConversation(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        conversationService.deleteConversationTemporarily(id, userDetails.getUser().getId());
        return ResponseEntity.ok("Delete conversation successfully");

    }

}

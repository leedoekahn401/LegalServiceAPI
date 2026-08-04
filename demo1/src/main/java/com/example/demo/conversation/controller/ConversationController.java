package com.example.demo.conversation.controller;

import com.example.demo.conversation.dto.ConversationResponseDTO;
import com.example.demo.conversation.service.ConversationService;
import com.example.demo.security.UserDetailsImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/me")
    public ResponseEntity<Page<ConversationResponseDTO>> getConversationForCurrentUser(Authentication authentication,
            Pageable pageable) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            Page<ConversationResponseDTO> conversations = conversationService
                    .getConversationsByUserId(userDetails.getUser().getId(), pageable);
            return ResponseEntity.ok(conversations);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponseDTO> getConversationById(@PathVariable UUID id,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            ConversationResponseDTO conversation = conversationService.getConversationById(id,
                    userDetails.getUser().getId());
            return ResponseEntity.ok(conversation);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
    }

    @PostMapping()
    public ResponseEntity<ConversationResponseDTO> createConversation(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            ConversationResponseDTO conversation = conversationService
                    .createConversation(userDetails.getUser().getId());
            return ResponseEntity.ok(conversation);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConversation(@PathVariable UUID id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            conversationService.deleteConversationTemporarily(id, userDetails.getUser().getId());
            return ResponseEntity.ok("Delete conversation successfully");
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
    }

}

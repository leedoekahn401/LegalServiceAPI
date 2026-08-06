package com.example.demo.message.controller;

import com.example.demo.message.dto.MessageResponseDTO;
import com.example.demo.message.dto.MessageSentDTO;
import com.example.demo.message.service.MessageService;
import com.example.demo.security.UserDetailsImpl;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Retrieves a paginated list of messages for a given conversation owned by the authenticated user.
     *
     * @param conversationId the conversation UUID
     * @param userDetails authenticated user details
     * @param pageable pagination parameters (page, size, sort)
     * @return paginated list of message response DTOs
     */
    @GetMapping
    public ResponseEntity<Page<MessageResponseDTO>> getMessagesByConversation(
            @RequestParam UUID conversationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        Page<MessageResponseDTO> messages = messageService.getMessagesByConversationId(
                conversationId, userDetails.getUser().getId(), pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * Sends a new message in a conversation and generates a mock bot response.
     *
     * @param userDetails authenticated user details
     * @param dto the message sent DTO payload
     * @return created user and bot message response DTOs with 201 Created status
     */
    @PostMapping
    public ResponseEntity<List<MessageResponseDTO>> sendMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody MessageSentDTO dto) {

        List<MessageResponseDTO> responses = messageService.sendMessage(dto, userDetails.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}

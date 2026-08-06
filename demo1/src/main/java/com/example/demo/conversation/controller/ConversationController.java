package com.example.demo.conversation.controller;

import com.example.demo.conversation.dto.ConversationResponseDTO;
import com.example.demo.conversation.dto.TitleChangeRequestDTO;
import com.example.demo.conversation.service.ConversationService;
import com.example.demo.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /**
     * Retrieves a paginated list of conversations owned by the currently
     * authenticated user.
     *
     * @param userDetails authenticated user details
     * @param pageable    pagination parameters (page, size, sort)
     * @return paginated list of conversation response DTOs
     */
    @GetMapping("/me")
    public ResponseEntity<Page<ConversationResponseDTO>> getConversationForCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        Page<ConversationResponseDTO> conversations = conversationService
                .getConversationsByUserId(userDetails.getUser().getId(), pageable);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Retrieves a specific conversation by ID for the authenticated user.
     *
     * @param id          the conversation UUID
     * @param userDetails authenticated user details
     * @return the conversation response DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponseDTO> getConversationById(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ConversationResponseDTO conversation = conversationService.getConversationById(id,
                userDetails.getUser().getId());
        return ResponseEntity.ok(conversation);

    }

    /**
     * Creates a new conversation for the authenticated user.
     *
     * @param userDetails authenticated user details
     * @return the created conversation response DTO with 201 Created status
     */
    @PostMapping()
    public ResponseEntity<ConversationResponseDTO> createConversation(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ConversationResponseDTO conversation = conversationService
                .createConversation(userDetails.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    /**
     * Soft deletes a conversation by ID for the authenticated user.
     *
     * @param id          the conversation UUID
     * @param userDetails authenticated user details
     * @return response entity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConversation(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        conversationService.deleteConversationTemporarily(id, userDetails.getUser().getId());
        return ResponseEntity.ok("Delete conversation successfully");

    }

    /**
     * Renames a conversation by ID for the authenticated user.
     *
     * @param id             the conversation UUID
     * @param userDetails    authenticated user details
     * @param titleChangeDTO the new title of the conversation
     * @return 204 No Content status
     */
    @PatchMapping("/{id}/title")
    public ResponseEntity<Void> renameConversation(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody TitleChangeRequestDTO titleChangeDTO) {
        conversationService.renameConversation(id, titleChangeDTO.title(),
                userDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }

}

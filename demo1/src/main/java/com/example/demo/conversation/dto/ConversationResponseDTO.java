package com.example.demo.conversation.dto;

import com.example.demo.conversation.entity.Conversation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConversationResponseDTO {
    private UUID id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ConversationResponseDTO fromEntity(Conversation conversation) {
        if (conversation == null) {
            return null;
        }
        ConversationResponseDTO dto = new ConversationResponseDTO();
        dto.setId(conversation.getId());
        dto.setTitle(conversation.getTitle());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        return dto;
    }
}

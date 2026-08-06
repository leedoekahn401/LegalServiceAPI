package com.example.demo.message.dto;

import com.example.demo.message.entity.*;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import com.example.demo.message.stratergy.MessageMetadata;

@Data
public class MessageResponseDTO {
    private UUID id;
    private UUID conversationId;
    private String content;
    private MessageMetadata metadata;
    private SenderType senderType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MessageResponseDTO fromEntity(Message message) {
        if (message == null) {
            return null;
        }
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setContent(message.getContent());
        dto.setSenderType(message.getSenderType());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        dto.setMetadata(message.getMetadata());

        return dto;
    }
}

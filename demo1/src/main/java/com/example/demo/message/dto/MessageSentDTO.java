package com.example.demo.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageSentDTO(
                @NotNull(message = "Conversation ID is required") UUID conversationId,

                @NotBlank(message = "Content is required") String content

) {
}

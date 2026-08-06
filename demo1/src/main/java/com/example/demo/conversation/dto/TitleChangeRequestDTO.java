package com.example.demo.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TitleChangeRequestDTO(
        @NotBlank(message = "Title cannot be blank") @Size(max = 255, message = "Title cannot exceed 255 characters") String title) {
}

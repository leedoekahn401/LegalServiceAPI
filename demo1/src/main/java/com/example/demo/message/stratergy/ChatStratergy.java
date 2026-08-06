package com.example.demo.message.stratergy;

import java.util.UUID;

import com.example.demo.message.dto.MessageResponseDTO;

public interface ChatStratergy {
    ChatResult processMessage(String message, UUID conversationId);

    String getChatType();

}

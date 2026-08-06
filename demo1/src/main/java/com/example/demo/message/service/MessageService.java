package com.example.demo.message.service;

import com.example.demo.conversation.entity.Conversation;
import com.example.demo.conversation.repository.ConversationRepo;
import com.example.demo.message.dto.MessageResponseDTO;
import com.example.demo.message.dto.MessageSentDTO;
import com.example.demo.message.entity.Message;
import com.example.demo.message.entity.SenderType;
import com.example.demo.message.repository.MessageRepo;
import com.example.demo.message.stratergy.ChatResult;
import com.example.demo.message.stratergy.ChatStratergy;
import com.example.demo.message.stratergy.MockChatStratergy;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepo messageRepo;
    private final ConversationRepo conversationRepo;

    public MessageService(MessageRepo messageRepo, ConversationRepo conversationRepo) {
        this.messageRepo = messageRepo;
        this.conversationRepo = conversationRepo;
    }

    public Page<MessageResponseDTO> getMessagesByConversationId(UUID conversationId, UUID userId, Pageable pageable) {
        conversationRepo.findByIdAndUserIdAndDeletedAtIsNull(conversationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

        Page<Message> messages = messageRepo
                .findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);

        return messages.map(MessageResponseDTO::fromEntity);
    }

    @Transactional
    public List<MessageResponseDTO> sendMessage(MessageSentDTO dto, UUID userId) {
        Conversation conversation = conversationRepo.findByIdAndUserIdAndDeletedAtIsNull(dto.conversationId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

        // Save User Message
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setSenderType(SenderType.USER);
        userMessage.setContent(dto.content());
        userMessage = messageRepo.save(userMessage);

        // Mock bot response
        ChatStratergy chatbot = new MockChatStratergy();
        ChatResult chatResult = chatbot.processMessage(dto.content(), dto.conversationId());

        // Save Bot Message
        Message botMessage = new Message();
        botMessage.setContent(chatResult.getAnswer());
        botMessage.setSenderType(SenderType.BOT);
        botMessage.setConversation(conversation);
        botMessage.setMetadata(chatResult.getMessageMetadata());
        botMessage = messageRepo.save(botMessage);

        return List.of(
                MessageResponseDTO.fromEntity(userMessage),
                MessageResponseDTO.fromEntity(botMessage));
    }
}

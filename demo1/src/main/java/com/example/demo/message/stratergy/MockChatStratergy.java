package com.example.demo.message.stratergy;

import java.util.UUID;

import com.example.demo.message.dto.MessageResponseDTO;

public class MockChatStratergy implements ChatStratergy {

    @Override
    public ChatResult processMessage(String message, UUID conversationId) {
        MessageMetadata metaData = new MessageMetadata();
        metaData.setModelUsed("mocked");
        metaData.setTotalTokens(0);
        metaData.setCitations(null);

        ChatResult chatResult = new ChatResult();
        chatResult.setAnswer("mocked response");
        chatResult.setMessageMetadata(metaData);

        return chatResult;
    }

    @Override
    public String getChatType() {
        // TODO Auto-generated method stub
        return "MOCK";
    }

}

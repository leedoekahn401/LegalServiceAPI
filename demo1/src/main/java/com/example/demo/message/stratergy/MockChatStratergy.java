package com.example.demo.message.stratergy;

import java.util.Collections;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class MockChatStratergy implements ChatStratergy {

    @Override
    public ChatResult processMessage(String message, UUID conversationId) {
        MessageMetadata metaData = new MessageMetadata();
        metaData.setModelUsed("mocked");
        metaData.setTotalTokens(0);
        metaData.setCitations(Collections.emptyList());

        ChatResult chatResult = new ChatResult();
        chatResult.setAnswer("Mock AI response to: " + message);
        chatResult.setMessageMetadata(metaData);

        return chatResult;
    }

    @Override
    public String getChatType() {
        return "MOCK";
    }

}


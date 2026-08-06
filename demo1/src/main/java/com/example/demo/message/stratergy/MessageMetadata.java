package com.example.demo.message.stratergy;

import java.util.List;
import lombok.Data;

@Data
public class MessageMetadata {
    private List<Citation> citations;
    private String modelUsed;
    private Integer totalTokens; // Optional: track API usage
}
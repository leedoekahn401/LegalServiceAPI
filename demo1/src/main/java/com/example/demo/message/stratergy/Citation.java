package com.example.demo.message.stratergy;

import java.util.UUID;

import lombok.Data;

@Data
public class Citation {
    private String sourceName; // e.g., "employee_handbook.pdf"
    private UUID documentId; // UUID cho vecbook
    private String externalUrl; // e.g., "https://company.com/hr/handbook"
    private String snippet; // The actual text chunk used
}
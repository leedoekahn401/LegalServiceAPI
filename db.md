erDiagram
    USERS {
        UUID id PK
        string role "ENUM: 'USER', 'ADMIN'"
        string email
        datetime created_at
        datetime updated_at
        datetime last_access_at
        datetime deleted_at
    }
    
    OAUTH_ACCOUNTS {
        UUID id PK
        UUID user_id FK
        string provider "E.g., 'google', 'github', 'apple'"
        string provider_user_id "Unique provider user ID (sub in JWT)"
        text access_token "Optional provider access token"
        datetime created_at
        datetime updated_at
    }
    
    CONVERSATIONS {
        UUID id PK
        UUID user_id FK
        string title
        datetime created_at
        datetime updated_at
    }
    
    MESSAGES {
        UUID id PK
        UUID conversation_id FK
        string sender_type "ENUM: 'USER', 'BOT'"
        text content
        json metadata "Stores document citations and references"
        datetime created_at
        datetime updated_at
    }
    
    DOCUMENTS {
        UUID id PK
        UUID uploader_id FK
        string file_name
        string s3_object_key "Reference to AWS S3 storage"
        string status "ENUM: 'PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'"
        datetime created_at
        datetime updated_at
        datetime deleted_at "For soft deletes"
    }

    REFRESH_TOKENS {
        UUID id PK
        UUID oauth_account_id FK
        string token_hash
        boolean is_revoked
        datetime expires_at
        datetime created_at
    }

    USERS ||--o{ OAUTH_ACCOUNTS : "links"
    OAUTH_ACCOUNTS ||--o{ REFRESH_TOKENS : "issues"
    USERS ||--o{ CONVERSATIONS : "initiates"
    CONVERSATIONS ||--o{ MESSAGES : "contains"
    USERS ||--o{ DOCUMENTS : "uploads (Admin only)"
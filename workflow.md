flowchart LR
    %% Actors
    User((User))
    Admin((Admin))

    %% Frontend Group
    subgraph Frontend ["Frontend (Next.js)"]
        direction TB
        FE["Frontend\n(Next.js)"]
        NextJS["Next.js"]
    end

    %% Core Services Group
    subgraph Core ["Core Services & API"]
        subgraph SpringBoot ["Spring Boot Backend (EC2)"]
            direction TB
            Auth["Auth (JWT)"]
            APIGW["API Gateway & Routing"]
            CRUD["CRUD Operations"]
            SQL["SQL Querying"]
        end
        RDS1[/"AWS RDS\n(MySQL)"/]
        S3_1[/"AWS S3\n(Save File)"/]
        RDS2[/"AWS RDS\n(MySQL)"/]
        S3_2[/"AWS S3\n(Object Storage)"/]
    end

    %% Queue Group
    subgraph Queue ["Asynchronous Workload Queue"]
        SQS["AWS SQS\n(Message Queue)"]
    end

    %% AI Backend Group
    subgraph AI ["AI & Heavy Workload Backend"]
        subgraph Python ["Python Backend (EC2 / FastAPI)"]
            direction TB
            GraphRAG["GraphRAG Logic\n(LlamaIndex)"]
            Workers["File Processing Workers"]
            Neo4jClient["Neo4j Client"]
        end
        KnowledgeAI["Knowledge AI API\n(Generate Cypher, Reasoning)"]
        ExternalAI(("External AI API\n(Google Gemini API)"))
        AuraDB[/"Knowledge Graph Store\nNeo4j (AuraDB)"/]
    end

    %% -------------------
    %% System Connections
    %% -------------------

    %% User and Frontend Logic
    User -- "1a." --> FE
    FE -- "1b." --> Auth
    APIGW -- "1h." --> FE
    Auth -- "1c." --> RDS1
    SQL -- "2c." --> S3_1

    %% Admin and File Upload Logic
    Admin -- "2a. Upload File" --> NextJS
    NextJS -- "1j." --> FE
    NextJS -- "1c. Save Chat" --> RDS2
    NextJS -- "Uploaded Files" --> S3_2
    RDS2 -- "Set PENDING" --> S3_2
    SpringBoot <-->|"Users, Sessions,\nChat History"| RDS2

    %% Queue and Storage Integration
    SpringBoot --> SQS
    SQS -- "2e." --> Workers
    S3_2 -- "2g. AWS S3\n(Download File)" --> Workers

    %% AI Workloads & Callbacks
    SpringBoot -- "1d. Python\n(FastAPI Request)" --> GraphRAG
    KnowledgeAI -- "1e." --> GraphRAG
    Workers -- "1f. Neo4j (Query)" --> KnowledgeAI
    Neo4jClient -- "2j. Spring Boot API\n(Update status COMPLETED)" --> SpringBoot

    %% External APIs & Knowledge Graph
    Neo4jClient <--> AuraDB
    Neo4jClient --> ExternalAI
    ExternalAI -- "2i. Neo4j\n(Build KG)" --> Neo4jClient
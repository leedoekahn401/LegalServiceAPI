# TODO: Conversation Domain Refactoring & Improvements

Refactoring and optimization tasks for the `com.example.demo.conversation` package based on code review findings.

- [ ] **Simplify Controller Authentication Boilerplate**
  - **Location**: [`ConversationController.java`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/conversation/controller/ConversationController.java)
  - **Task**: Replace manual `Authentication` checking and `UserDetailsImpl` casting across all endpoints with `@AuthenticationPrincipal UserDetailsImpl userDetails`.

- [ ] **Adhere to RESTful HTTP Status & Response Standards**
  - **Location**: [`ConversationController.java`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/conversation/controller/ConversationController.java)
  - **Tasks**:
    - Change `POST /api/conversations` response status from `200 OK` to `201 Created` (`HttpStatus.CREATED`).
    - Change `DELETE /api/conversations/{id}` response from raw String to `204 No Content` (`ResponseEntity<Void>`).

- [ ] **Modernize DTOs with Java Records**
  - **Location**: [`ConversationResponseDTO.java`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/conversation/dto/ConversationResponseDTO.java)
  - **Task**: Refactor `ConversationResponseDTO` from a Lombok `@Data` class to an immutable Java `record`.

- [ ] **Decouple Service Layer from Web/HTTP Exceptions**
  - **Location**: [`ConversationService.java`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/conversation/service/ConversationService.java)
  - **Task**: Replace direct `ResponseStatusException` instantiation in `ConversationService` with custom domain exceptions handled by `@RestControllerAdvice`.

- [ ] **Clean Repository Query Method Names**
  - **Location**: [`ConversationRepo.java`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/conversation/repository/ConversationRepo.java)
  - **Task**: Review `findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc` method name to prevent sorting redundancy when passing dynamic `Pageable`.

---

# TODO: Message Domain API Implementation

Implementation tasks for building end-to-end RESTful Message APIs in `com.example.demo.message`.

- [ ] **Create Message Repository**
  - **Location**: [`MessageRepo.java`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/message/repository/MessageRepo.java)
  - **Task**: Create `MessageRepo` extending `JpaRepository<Message, UUID>` with query methods for fetching paginated messages by `conversationId` while verifying conversation ownership.

- [ ] **Create Java Record DTOs**
  - **Location**: `com.example.demo.message.dto`
  - **Tasks**:
    - Create `CreateMessageRequestDTO` Java record with validation annotations (`@NotBlank`).
    - Create `MessageResponseDTO` Java record (`id`, `conversationId`, `senderType`, `content`, `metadata`, `createdAt`) with a static `fromEntity(Message)` mapper method.

- [ ] **Implement Service Layer Logic**
  - **Location**: [`MessageService.java`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/message/service/MessageService.java)
  - **Tasks**:
    - `sendMessage(...)`: Validate user ownership of target conversation, persist user message, touch/update parent conversation timestamp, and handle bot/AI response logic.
    - `getMessagesByConversation(...)`: Validate ownership and return paginated `Page<MessageResponseDTO>`.
    - Enforce proper `@Transactional` boundaries and custom domain exceptions.

- [ ] **Build RESTful Controller**
  - **Location**: [`MessageController.java`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/message/controller/MessageController.java)
  - **Tasks**:
    - Implement `POST /api/conversations/{conversationId}/messages` returning `201 Created`.
    - Implement `GET /api/conversations/{conversationId}/messages` returning `200 OK` with pagination (`Pageable`).
    - Use `@AuthenticationPrincipal UserDetailsImpl userDetails` for principal injection.

- [ ] **Enforce Security & Data Isolation**
  - **Location**: Security Config & `MessageService`
  - **Task**: Ensure users can only interact with messages belonging to their own non-deleted conversations.


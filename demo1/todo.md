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

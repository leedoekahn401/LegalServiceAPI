# Adding a New API in Spring Boot

To create a new API in your Spring Boot project, generally follow the standard layered architecture procedure (Controller -> Service -> Repository).

Here is the step-by-step procedure based on the structure of your project:

## 1. Update the Repository (Data Access Layer)

If your new API requires fetching or modifying data in the database using a query that doesn't exist yet, you first need to add it to your repository interface (e.g., `UserRepo.java`).

```java
// Example: Adding a custom query to find by name
Optional<User> findByName(String name);
```

## 2. Define the Request/Response DTOs (Optional)

If your API requires a specific incoming JSON payload or returns a specific structure that isn't just an entity, create or update a Data Transfer Object (DTO) in the `dto` package (e.g., `UserDTO.java`).

```java
// Example: Creating a new request payload for creating a user
@Data
public class CreateUserRequestDTO {
    private String name;
    private String email;
}
```

## 3. Implement Business Logic in the Service Layer

Next, add the core business logic to your service class (e.g., `UserService.java`). The service method should call the repository, perform any necessary validations, and usually map the database `Entity` into a `DTO` to be returned.

```java
public UserDTO getUserByName(String name) {
    User user = userRepo.findByName(name)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    return UserDTO.fromEntity(user);
}
```

## 4. Create the Endpoint in the Controller Layer

Finally, expose the service method as a REST API endpoint in your controller (e.g., `UserController.java`). 

- Use standard Spring annotations like `@GetMapping`, `@PostMapping`, `@PutMapping`, or `@DeleteMapping`.
- Map your inputs using `@PathVariable` (for `/api/users/{id}`), `@RequestParam` (for `/api/users?name=John`), or `@RequestBody` (for JSON payloads).
- **Security:** If the endpoint needs to be restricted, apply your security annotations like `@PreAuthorize("hasRole('ADMIN')")`.
- Wrap the return value in a `ResponseEntity`.

```java
/**
 * Get user information by name.
 */
@GetMapping("/by-name")
@PreAuthorize("hasRole('ADMIN')") // Add security if needed
public ResponseEntity<UserDTO> getUserByName(@RequestParam String name) {
    UserDTO userDTO = userService.getUserByName(name);
    return ResponseEntity.ok(userDTO);
}
```

## 5. Integrating with External Services (Outbound Communication)

If your backend needs to communicate with *other external services* (e.g., third-party APIs or other microservices), this logic should **not** go directly in the Controller or Repository. Instead, it belongs in a dedicated **Client** or **Integration** layer.

1. **Create a Client Class**: Create a package like `com.example.demo.client` (or `integration`).
2. **Use an HTTP Client**: Make the HTTP calls using Spring's `RestTemplate`, `WebClient` (for WebFlux/reactive), `RestClient` (Spring Boot 3.2+), or declarative clients like `OpenFeign`.
3. **Call from the Service**: Your core business `Service` will inject this Client and call its methods, keeping business logic separate from HTTP request mechanics.

```java
// Example: External Client in com.example.demo.client
@Component
public class PaymentServiceClient {
    private final RestClient restClient;

    public PaymentServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://api.external-payment.com").build();
    }

    public PaymentStatus getStatus(String transactionId) {
        return restClient.get()
                .uri("/status/{id}", transactionId)
                .retrieve()
                .body(PaymentStatus.class);
    }
}
```

Then, you inject `PaymentServiceClient` into your `Service` class just like a Repository.

## Summary of the Flow

**Client Request** $\rightarrow$ **Controller** (Validates input, checks security) $\rightarrow$ **Service** (Business logic) $\rightarrow$ **Repository** (Database) OR **Client** (External API)

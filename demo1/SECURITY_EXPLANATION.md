# Spring Security & OAuth2 JWT Architecture Documentation

This document explains how the authentication and authorization mechanism works in [`com.example.demo.security`](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security).

---

## 1. Overview & Architecture

The application uses a **Stateless Hybrid Security Architecture** combining **OAuth2 Social Login** (e.g. Google) and **JSON Web Tokens (JWT)** for API authorization.

```
                                  ┌───────────────────────────┐
                                  │    HTTP Request Filter    │
                                  └─────────────┬─────────────┘
                                                │
                 ┌──────────────────────────────┴──────────────────────────────┐
                 │                                                             │
         [ OAuth2 Login Flow ]                                         [ Bearer JWT Flow ]
                 │                                                             │
   1. User logs in via Google                                 1. Client sends header `Bearer <JWT>`
   2. OAuth2UserService fetches user                          2. JwtAuthenticationFilter extracts token
   3. Saves/updates User entity                               3. JwtTokenHelper validates signature
   4. SuccessHandler generates JWT                            4. CustomUserDetailsService loads User
   5. Redirects to frontend with token                        5. User authenticated in SecurityContext
```

---

## 2. Key Components Breakdown

### 1. [SecurityConfig.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/SecurityConfig.java)
Core configuration class annotated with `@EnableWebSecurity` and `@EnableMethodSecurity`.
* **Stateless Session**: Configured with `SessionCreationPolicy.STATELESS` because requests rely on JWT tokens rather than HTTP server sessions.
* **CSRF Disabled**: CSRF protection is disabled (`csrf.disable()`) for stateless REST APIs.
* **Public Endpoints**: `/`, `/login`, `/error`, and `/oauth2/**` are accessible without authentication (`permitAll()`). All other routes require authentication.
* **OAuth2 Integration**: Uses [OAuth2UserService.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/OAuth2UserService.java) for fetching user info and [OAuth2AuthenticationSuccessHandler.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/OAuth2AuthenticationSuccessHandler.java) upon successful authentication.
* **JWT Filter**: Registers [JwtAuthenticationFilter.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/auth/JwtAuthenticationFilter.java) prior to `UsernamePasswordAuthenticationFilter`.

### 2. [UserDetailsImpl.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/UserDetailsImpl.java)
Bridge object implementing both `OAuth2User` and Spring Security's `UserDetails`.
* Wraps the domain model entity [User.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/user/entity/User.java).
* Converts internal user roles into GrantedAuthorities prefixed with `ROLE_` (e.g. `ROLE_ADMIN`, `ROLE_USER`).
* Account status checks (`isEnabled()`) verify soft deletion (`user.getDeletedAt() == null`).

### 3. [CustomUserDetailsService.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/CustomUserDetailsService.java)
Service implementing Spring's `UserDetailsService`.
* `loadUserByUsername(String email)`: Finds user by email in database.
* `loadUserById(UUID id)`: Finds user by UUID when parsing incoming JWT payload claims.
* Returns wrapped [UserDetailsImpl.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/UserDetailsImpl.java) instances.

### 4. [JwtTokenHelper.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/JwtTokenHelper.java)
Utility component for JWT operations powered by standard JJWT (`io.jsonwebtoken`):
* **Key Derivation**: Computes HMAC-SHA256 secret key from configuration property `${app.jwtSecret}`.
* **Token Creation (`generateToken`)**: Embeds user UUID as JWT `subject` and `role` as custom claim, signing with HS256 algorithm and setting 24h expiration (`${app.jwtExpirationMs}`).
* **Token Validation (`validateToken`)**: Parses and checks signature and expiration validity.
* **User ID Extraction (`getUserIdFromJWT`)**: Decodes claims to extract user UUID string.

### 5. [OAuth2UserService.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/OAuth2UserService.java)
Extends `DefaultOAuth2UserService`:
* Runs after third-party OAuth2 authorization succeeds (e.g., via Google).
* Extracts user profile info (`email`, `name`, `sub`/`id`).
* Calls `AuthAccountService.processOAuthPostLogin(...)` to sync/persist local database user records.
* Returns a new [UserDetailsImpl.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/UserDetailsImpl.java).

### 6. [OAuth2AuthenticationSuccessHandler.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/OAuth2AuthenticationSuccessHandler.java)
Extends `SimpleUrlAuthenticationSuccessHandler`:
* Triggered automatically upon successful OAuth2 authentication.
* Uses [JwtTokenHelper.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/JwtTokenHelper.java) to mint a JWT token.
* Appends token as query parameter and redirects user to frontend client (e.g., `http://localhost:3000/oauth2/redirect?token=<JWT>`).

### 7. [JwtAuthenticationFilter.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/auth/JwtAuthenticationFilter.java)
HTTP filter extending `OncePerRequestFilter`:
* Intercepts incoming requests to extract `Authorization` header (`Bearer <JWT>`).
* Validates token and extracts user ID.
* Loads user entity using `CustomUserDetailsService.loadUserById(id)`.
* Places `UsernamePasswordAuthenticationToken` inside Spring's `SecurityContextHolder` to authenticate the request lifecycle.

---

## 3. Step-by-Step Execution Workflows

### Flow 1: OAuth2 Social Authentication
1. **User Request**: User navigates to `/oauth2/authorization/google`.
2. **Provider Authorization**: User authenticates with Google consent screen.
3. **User Processing**: Google redirects code to backend. [OAuth2UserService.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/OAuth2UserService.java) executes to create/update local database user records.
4. **JWT Handshake**: [OAuth2AuthenticationSuccessHandler.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/OAuth2AuthenticationSuccessHandler.java) generates JWT and redirects to frontend endpoint with token.

### Flow 2: Authenticated API Requests
1. **Request Header**: Frontend attaches `Authorization: Bearer <JWT>` header to API requests.
2. **Filter Interception**: [JwtAuthenticationFilter.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/auth/JwtAuthenticationFilter.java) validates signature & expiry via [JwtTokenHelper.java](file:///c:/Document/UltPrj/service_api/demo1/src/main/java/com/example/demo/security/JwtTokenHelper.java).
3. **Security Context Creation**: Loads `UserDetails` from database and populates `SecurityContextHolder`.
4. **Endpoint Access**: Spring Security allows access to protected REST endpoints based on roles and authentication status.

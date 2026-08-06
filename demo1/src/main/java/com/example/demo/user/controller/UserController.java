package com.example.demo.user.controller;

import com.example.demo.security.UserDetailsImpl;
import com.example.demo.user.dto.UserFullInfoDTO;
import com.example.demo.user.dto.UserInfoDTO;
import com.example.demo.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a paginated list of all users. Accessible by ADMIN role only.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return paginated list of user full info DTOs
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserFullInfoDTO>> getAllUsers(Pageable pageable) {
        Page<UserFullInfoDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves basic information of the currently authenticated user.
     *
     * @param userDetails authenticated user details
     * @return user info DTO of the current user
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoDTO userDTO = userService.getUserBasicInfo(userDetails.getUser());
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Retrieves user information by user ID. Accessible by ADMIN role only.
     *
     * @param id the user UUID
     * @return user info DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoDTO> getUserById(@PathVariable UUID id) {
        UserInfoDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Retrieves user information by email address. Accessible by ADMIN role only.
     *
     * @param email the email address of the user
     * @return user info DTO
     */
    @GetMapping("/by-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoDTO> getUserByEmail(@RequestParam String email) {
        UserInfoDTO userDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDTO);
    }
}

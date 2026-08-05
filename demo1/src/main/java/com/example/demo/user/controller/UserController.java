package com.example.demo.user.controller;

import com.example.demo.security.UserDetailsImpl;
import com.example.demo.user.dto.UserFullInfoDTO;
import com.example.demo.user.dto.UserInfoDTO;
import com.example.demo.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserFullInfoDTO>> getAllUsers(Pageable pageable) {
        Page<UserFullInfoDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get information of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoDTO userDTO = userService.getUserBasicInfo(userDetails.getUser());
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Get user information by user ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoDTO> getUserById(@PathVariable UUID id) {
        UserInfoDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Get user information by email address.
     */
    @GetMapping("/by-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoDTO> getUserByEmail(@RequestParam String email) {
        UserInfoDTO userDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDTO);
    }
}

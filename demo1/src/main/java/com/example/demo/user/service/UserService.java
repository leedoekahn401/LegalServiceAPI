package com.example.demo.user.service;

import com.example.demo.user.dto.UserBasicInfoDTO;
import com.example.demo.user.dto.UserFullInfoDTO;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional // write operation need transaction
    public User createUser(OAuth2User oAuth2User) {
        User user = new User();
        user.setName(oAuth2User.getAttribute("name"));
        user.setEmail(oAuth2User.getAttribute("email"));
        return userRepo.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserFullInfoDTO> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(UserFullInfoDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public UserFullInfoDTO getUserById(UUID id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return UserFullInfoDTO.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserFullInfoDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));
        return UserFullInfoDTO.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserBasicInfoDTO getUserBasicInfo(User user) {
        return UserBasicInfoDTO.fromEntity(user);
    }
}


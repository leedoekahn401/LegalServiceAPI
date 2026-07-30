package com.example.demo.auth.service;

import com.example.demo.auth.entity.OAuthAccount;
import com.example.demo.auth.repository.OAuthAccountRepo;
import com.example.demo.user.entity.Role;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthAccountService {

    private final UserRepo userRepo;
    private final OAuthAccountRepo oAuthAccountRepo;

    public AuthAccountService(UserRepo userRepo, OAuthAccountRepo oAuthAccountRepo) {
        this.userRepo = userRepo;
        this.oAuthAccountRepo = oAuthAccountRepo;
    }

    @Transactional
    public User processOAuthPostLogin(String email, String name, String provider, String providerUserId) {
        Optional<OAuthAccount> optionalAccount = oAuthAccountRepo.findByProviderAndProviderUserId(provider, providerUserId);

        if (optionalAccount.isPresent()) {
            return optionalAccount.get().getUser();
        }

        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole(Role.USER);
            return userRepo.save(newUser);
        });

        OAuthAccount newAccount = new OAuthAccount();
        newAccount.setUser(user);
        newAccount.setProvider(provider);
        newAccount.setProviderUserId(providerUserId);
        oAuthAccountRepo.save(newAccount);

        return user;
    }
}

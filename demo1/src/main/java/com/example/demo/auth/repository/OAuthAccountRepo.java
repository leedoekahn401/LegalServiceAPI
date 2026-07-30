package com.example.demo.auth.repository;

import com.example.demo.auth.entity.OAuthAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthAccountRepo extends JpaRepository<OAuthAccount, UUID> {
    @EntityGraph(attributePaths = {"user"})
    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);
}

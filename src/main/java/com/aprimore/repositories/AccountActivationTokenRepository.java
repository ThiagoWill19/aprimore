package com.aprimore.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprimore.models.AccountActivationToken;

public interface AccountActivationTokenRepository extends JpaRepository<AccountActivationToken, UUID> {
    Optional<AccountActivationToken> findByTokenHash(String tokenHash);
}

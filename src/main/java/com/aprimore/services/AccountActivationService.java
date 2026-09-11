package com.aprimore.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.models.AccountActivationToken;
import com.aprimore.models.User;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.repositories.AccountActivationTokenRepository;
import com.aprimore.repositories.UserRepository;

@Service
public class AccountActivationService {
    private static final Duration TOKEN_VALIDITY = Duration.ofHours(24);

    private final SecureRandom secureRandom = new SecureRandom();
    private final AccountActivationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String baseUrl;

    public AccountActivationService(AccountActivationTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.base-url}") String baseUrl) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public String createActivationLink(User user) {
        String rawToken = generateToken();
        tokenRepository.save(new AccountActivationToken(user, hash(rawToken), Instant.now().plus(TOKEN_VALIDITY)));
        return baseUrl + "/activate-account?token=" + rawToken;
    }

    public boolean isTokenUsable(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }
        return tokenRepository.findByTokenHash(hash(rawToken))
                .map(token -> token.isUsable(Instant.now()))
                .orElse(false);
    }

    @Transactional
    public void activateAccount(String rawToken, String password) {
        AccountActivationToken token = tokenRepository.findByTokenHash(hash(rawToken))
                .filter(value -> value.isUsable(Instant.now()))
                .orElseThrow(() -> new DomainRuleException("Este link e invalido, expirou ou ja foi utilizado."));

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(password));
        user.setAccountStatus(AccountStatus.ACTIVE);
        token.markAsUsed(Instant.now());
        userRepository.save(user);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(64);
            for (byte valueByte : digest) {
                result.append(String.format("%02x", valueByte));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }
}

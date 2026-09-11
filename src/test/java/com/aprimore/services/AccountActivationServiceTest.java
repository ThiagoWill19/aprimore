package com.aprimore.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aprimore.models.AccountActivationToken;
import com.aprimore.models.User;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.repositories.AccountActivationTokenRepository;
import com.aprimore.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AccountActivationServiceTest {

    @Mock
    private AccountActivationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void activateAccount_ShouldSetPasswordActivateUserAndConsumeToken() {
        AccountActivationService service = new AccountActivationService(tokenRepository, userRepository, passwordEncoder,
                "https://app.aprimore.com/");
        User user = new User();
        user.setAccountStatus(AccountStatus.INACTIVE);

        String link = service.createActivationLink(user);
        ArgumentCaptor<AccountActivationToken> tokenCaptor = ArgumentCaptor.forClass(AccountActivationToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        AccountActivationToken token = tokenCaptor.getValue();
        String rawToken = link.substring(link.indexOf("token=") + 6);

        when(tokenRepository.findByTokenHash(token.getTokenHash())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("Senha@123")).thenReturn("senha-hash");

        service.activateAccount(rawToken, "Senha@123");

        assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
        assertEquals("senha-hash", user.getPassword());
        assertTrue(token.getUsedAt().isAfter(Instant.now().minusSeconds(5)));
        verify(userRepository).save(user);
    }

    @Test
    void isTokenUsable_ShouldRejectBlankToken() {
        AccountActivationService service = new AccountActivationService(tokenRepository, userRepository, passwordEncoder,
                "https://app.aprimore.com");

        assertFalse(service.isTokenUsable(" "));
    }
}

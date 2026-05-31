package com.aprimore.services;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.User;
import com.aprimore.models.dtos.UserDto;
import com.aprimore.models.dtos.UserToListDto;
import com.aprimore.models.mappers.UserMapper;
import com.aprimore.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Deve criar um novo usuário")
    void newUser_Success() {
        User user = new User();
        userService.newUser(user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve listar todos os usuários de uma empresa")
    void findAllByBusiness_Success() {
        UUID businessId = UUID.randomUUID();
        when(userRepository.findAllByBusinessIdOrderByName(businessId)).thenReturn(List.of(new User()));
        when(userMapper.mapToUserToListDto(any(User.class))).thenReturn(new UserToListDto());

        List<UserToListDto> result = userService.findAllByBusiness(businessId);

        assertFalse(result.isEmpty());
        verify(userRepository).findAllByBusinessIdOrderByName(businessId);
        verify(userMapper).mapToUserToListDto(any(User.class));
    }

    @Test
    @DisplayName("Deve encontrar um usuário por ID")
    void findUserById_Success() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(userMapper.mapToUserDto(any(User.class))).thenReturn(new UserDto());

        UserDto result = userService.findUserById(userId);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário por ID inexistente")
    void findUserById_ShouldThrowResourceNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(userId));
    }

    @Test
    @DisplayName("Deve alterar a senha com sucesso")
    void alterPassword_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setPassword("encoded_old_password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old_password", "encoded_old_password")).thenReturn(true);
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_new_password");

        userService.alterPassword(userId, "old_password", "new_password");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded_new_password", userCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar senha com senha antiga incorreta")
    void alterPassword_ShouldThrowDomainRuleException_WhenOldPasswordIsIncorrect() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setPassword("encoded_old_password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_old_password", "encoded_old_password")).thenReturn(false);

        assertThrows(DomainRuleException.class, () -> userService.alterPassword(userId, "wrong_old_password", "new_password"));
        verify(userRepository, never()).save(any());
    }
}
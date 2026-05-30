package com.aprimore.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Address;
import com.aprimore.models.Business;
import com.aprimore.models.Client;
import com.aprimore.models.User;
import com.aprimore.models.dtos.UpdateAddressDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.mappers.AddressMapper;
import com.aprimore.repositories.AddressRepository;
import com.aprimore.repositories.ClientRepository;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private Client client;
    private Business business;
    private Address address;
    private UpdateAddressDto dto;
    private UUID businessId;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        business = new Business();
        business.setId(businessId);
        business.setAccountStatus(AccountStatus.ACTIVE);

        user = new User();
        user.setBusiness(business);

        client = new Client();
        client.setId(clientId);
        client.setBusiness(business);

        address = new Address();
        client.setAddress(address);

        dto = new UpdateAddressDto();
        dto.setClientId(clientId);
    }

    @Test
    @DisplayName("Deve criar endereço com sucesso para um cliente válido")
    void shouldCreateAddressSuccessfully() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act
        addressService.createAddress(clientId, address, user);

        // Assert
        verify(addressRepository).save(address);
    }

    @Test
    @DisplayName("[Criação] Deve negar acesso se o usuário pertence a outra empresa")
    void shouldThrowAccessDeniedWhenUserBelongsToDifferentBusinessInCreateAddress() {
        // Arrange
        Business anotherBusiness = new Business();
        anotherBusiness.setId(UUID.randomUUID());
        user.setBusiness(anotherBusiness); // Usuário de outra empresa

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> addressService.createAddress(clientId, address, user));
        verify(addressRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Criação] Deve negar acesso se a empresa estiver inativa")
    void shouldThrowAccessDeniedWhenBusinessIsInactiveInCreateAddress() {
        // Arrange
        business.setAccountStatus(AccountStatus.INACTIVE);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> addressService.createAddress(clientId, address, user));
        verify(addressRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Criação] Deve lançar exceção se o cliente não for encontrado")
    void shouldThrowResourceNotFoundWhenClientDoesNotExistInCreateAddress() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> addressService.createAddress(clientId, address, user));
        verify(addressRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar o endereço com sucesso")
    void shouldUpdateAddressSuccessfully() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        assertDoesNotThrow(() -> addressService.updateAddress(dto, user));

        // Verify
        verify(addressMapper).updateAddressFromDto(dto, address);
        verify(addressRepository).save(address);
    }

    @Test
    @DisplayName("[Atualização] Deve negar acesso se o usuário pertence a outra empresa")
    void shouldThrowAccessDeniedWhenUserBelongsToDifferentBusinessInUpdateAddress() {
        // Arrange
        Business anotherBusiness = new Business();
        anotherBusiness.setId(UUID.randomUUID());
        user.setBusiness(anotherBusiness); // Usuário de outra empresa

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> addressService.updateAddress(dto, user));
        verify(addressRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Atualização] Deve negar acesso se a empresa estiver inativa")
    void shouldThrowAccessDeniedWhenBusinessIsInactiveInUpdateAddress() {
        // Arrange
        business.setAccountStatus(AccountStatus.INACTIVE);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> addressService.updateAddress(dto, user));
        verify(addressRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Atualização] Deve lançar exceção se o cliente não for encontrado")
    void shouldThrowResourceNotFoundWhenClientDoesNotExistInUpdateAddress() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> addressService.updateAddress(dto, user));
        verify(addressRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Atualização] Deve lançar exceção se o cliente não possuir endereço")
    void shouldThrowResourceNotFoundWhenClientHasNoAddressInUpdateAddress() {
        // Arrange
        client.setAddress(null);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> addressService.updateAddress(dto, user));
        verify(addressRepository, never()).save(any());
    }
}
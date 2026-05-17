package com.aprimore.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

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
public class AddressServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;



    @Test
    void shouldCreateAddressSuccessfully() {

        UUID clientId = UUID.randomUUID();
        
        Business business = new Business();
        business.setId(UUID.randomUUID());
        business.setAccountStatus(AccountStatus.ACTIVE);
        
        Client client = new Client();
        client.setId(clientId);
        client.setBusiness(business);

        User user = new User();
        user.setBusiness(business);

        Address address = new Address();

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        addressService.createAddress(clientId, address, user);

        verify(addressRepository).save(address);
    }

    @Test
    void shouldThrowAccessDeniedWhenUserBelongsToDifferentBusinessInCreateAddress() {

        UUID clientId = UUID.randomUUID();
        
        Business business1 = new Business();
        business1.setId(UUID.randomUUID());
        business1.setAccountStatus(AccountStatus.ACTIVE);
        
        Business business2 = new Business();
        business2.setId(UUID.randomUUID());
        business2.setAccountStatus(AccountStatus.ACTIVE);
        
        Client client = new Client();
        client.setId(clientId);
        client.setBusiness(business1);

        User user = new User();
        user.setBusiness(business2);

        Address address = new Address();

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThrows(AccessDeniedException.class, () -> {
            addressService.createAddress(clientId, address, user);
        });

        verify(addressRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowAccessDeniedWhenBusinessIsInactiveInCreateAddress() {

        UUID clientId = UUID.randomUUID();
        
        Business business = new Business();
        business.setId(UUID.randomUUID());
        business.setAccountStatus(AccountStatus.INACTIVE);
        
        Client client = new Client();
        client.setId(clientId);
        client.setBusiness(business);

        User user = new User();
        user.setBusiness(business);

        Address address = new Address();

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThrows(AccessDeniedException.class, () -> {
            addressService.createAddress(clientId, address, user);
        });

        verify(addressRepository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundWhenClientDoesNotExistInCreateAddress() {

        UUID clientId = UUID.randomUUID();
        
        Business business = new Business();
        business.setId(UUID.randomUUID());
        business.setAccountStatus(AccountStatus.ACTIVE);
        
        User user = new User();
        user.setBusiness(business);

        Address address = new Address();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.createAddress(clientId, address, user);
        });

        verify(addressRepository, never()).save(any());
    }

    @Test
    void shouldUpdateAddressSuccessfully() {

        UUID clientId = UUID.randomUUID();
        
        Business business = new Business();
        business.setId(UUID.randomUUID());
        business.setAccountStatus(AccountStatus.ACTIVE);
        
        Client client = new Client();
        client.setId(clientId);
        client.setBusiness(business);
        
        Address address = new Address();
        client.setAddress(address);

        User user = new User();
        user.setBusiness(business);

        UpdateAddressDto dto = new UpdateAddressDto();
        dto.setClientId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        addressService.updateAddress(dto, user);

        verify(addressRepository).save(address);
    }

    @Test
    void shouldThrowAccessDeniedWhenUserBelongsToDifferentBusinessInUpdateAddress() {
        UUID clientId = UUID.randomUUID();
        
        Business business1 = new Business();
        business1.setId(UUID.randomUUID());
        business1.setAccountStatus(AccountStatus.ACTIVE);
        
        Business business2 = new Business();
        business2.setId(UUID.randomUUID());
        business2.setAccountStatus(AccountStatus.ACTIVE);
        
        Client client = new Client();
        client.setId(clientId);
        client.setBusiness(business1);
        
        Address address = new Address();
        client.setAddress(address);

        User user = new User();
        user.setBusiness(business2);

        UpdateAddressDto dto = new UpdateAddressDto();
        dto.setClientId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThrows(AccessDeniedException.class, () -> {
            addressService.updateAddress(dto, user);
        });

        verify(addressRepository, never()).save(any());
    }

        @Test
    void shouldThrowAccessDeniedWhenBusinessIsInactiveInUpdateAddress() {

        UUID clientId = UUID.randomUUID();
        
        Business business = new Business();
        business.setId(UUID.randomUUID());
        business.setAccountStatus(AccountStatus.INACTIVE);
        
        Client client = new Client();
        client.setId(clientId);
        client.setBusiness(business);
        
        Address address = new Address();
        client.setAddress(address);

        User user = new User();
        user.setBusiness(business);

        UpdateAddressDto dto = new UpdateAddressDto();
        dto.setClientId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThrows(AccessDeniedException.class, () -> {
            addressService.updateAddress(dto, user);
        });

        verify(addressRepository, never()).save(any());
    }

        @Test
    void shouldThrowResourceNotFoundWhenClientDoesNotExistInUpdateAddress() {

        UUID clientId = UUID.randomUUID();
        
        Business business = new Business();
        business.setId(UUID.randomUUID());
        business.setAccountStatus(AccountStatus.ACTIVE);
        
        User user = new User();
        user.setBusiness(business);

        UpdateAddressDto dto = new UpdateAddressDto();
        dto.setClientId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.updateAddress(dto, user);
        });

        verify(addressRepository, never()).save(any());
        
    }

    @Test
    void shouldThrowResourceNotFoundWhenClientHasNoAddressInUpdateAddress() {

        UUID clientId = UUID.randomUUID();
        
        Business business = new Business();
        business.setId(UUID.randomUUID());
        business.setAccountStatus(AccountStatus.ACTIVE);
        
        Client client = new Client();
        client.setId(clientId);
        client.setBusiness(business);
        client.setAddress(null);

        User user = new User();
        user.setBusiness(business);

        UpdateAddressDto dto = new UpdateAddressDto();
        dto.setClientId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.updateAddress(dto, user);
        });

        verify(addressRepository, never()).save(any());
    }

    
}
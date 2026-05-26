package com.aprimore.services;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Business;
import com.aprimore.models.Client;
import com.aprimore.models.User;
import com.aprimore.models.dtos.ClientDetailsDto;
import com.aprimore.models.dtos.NewClientDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.mappers.ClientMapper;
import com.aprimore.repositories.BusinessRepository;
import com.aprimore.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private User user;
    private Business business;
    private Client client;
    private NewClientDto newClientDto;
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

        newClientDto = new NewClientDto();
        newClientDto.setClientName("Cliente Teste");
        newClientDto.setCnpj("11.222.333/0001-44");
        newClientDto.setClientEmail("cliente@teste.com");
        newClientDto.setClientPhoneNumber("11999998888");
    }

    @Test
    @DisplayName("Deve criar um novo cliente com sucesso")
    void newClient_Success() {
        // Arrange
        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));
        when(clientRepository.existsByBusinessIdAndClientEmail(any(), any())).thenReturn(false);
        when(clientRepository.existsByBusinessIdAndCnpj(any(), any())).thenReturn(false);
        when(clientMapper.mapToClient(newClientDto)).thenReturn(new Client());
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Act
        Client result = clientService.newClient(newClientDto, businessId);

        // Assert
        assertNotNull(result);
        verify(clientRepository).save(any(Client.class));
        assertEquals("11222333000144", newClientDto.getCnpj()); // Verifica se limpou a máscara
    }

    @Test
    @DisplayName("[Criação] Deve negar acesso se a empresa estiver inativa")
    void newClient_ShouldThrowAccessDenied_WhenBusinessIsInactive() {
        // Arrange
        business.setAccountStatus(AccountStatus.INACTIVE);
        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> clientService.newClient(newClientDto, businessId));
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Criação] Deve lançar exceção se o email do cliente já existir")
    void newClient_ShouldThrowDomainRuleException_WhenEmailExists() {
        // Arrange
        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));
        when(clientRepository.existsByBusinessIdAndClientEmail(businessId, newClientDto.getClientEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> clientService.newClient(newClientDto, businessId));
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Criação] Deve lançar exceção se o CNPJ do cliente já existir")
    void newClient_ShouldThrowDomainRuleException_WhenCnpjExists() {
        // Arrange
        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));
        when(clientRepository.existsByBusinessIdAndClientEmail(any(), any())).thenReturn(false);
        when(clientRepository.existsByBusinessIdAndCnpj(any(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> clientService.newClient(newClientDto, businessId));
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar detalhes do cliente por ID com sucesso")
    void findById_Success() throws Exception {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientMapper.mapToClientDetailsDto(client)).thenReturn(new ClientDetailsDto());

        // Act
        ClientDetailsDto result = clientService.findById(clientId, user);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getQntMachines());
        assertEquals(0, result.getQntServiceOrder());
    }

    @Test
    @DisplayName("[Busca] Deve lançar exceção se o cliente não for encontrado")
    void findById_ShouldThrowResourceNotFound() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> clientService.findById(clientId, user));
    }

    @Test
    @DisplayName("[Busca] Deve negar acesso se o cliente pertencer a outra empresa")
    void findById_ShouldThrowAccessDenied_WhenClientBelongsToAnotherBusiness() {
        // Arrange
        Business anotherBusiness = new Business();
        anotherBusiness.setId(UUID.randomUUID());
        client.setBusiness(anotherBusiness);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> clientService.findById(clientId, user));
    }

    @Test
    @DisplayName("Deve listar todos os clientes da empresa sem filtro de busca")
    void findAllClientsByBusiness_NoSearch() {
        // Arrange
        Page<Client> page = new PageImpl<>(List.of(new Client()));
        when(clientRepository.findByBusinessIdOrderByClientName(eq(businessId), any(Pageable.class))).thenReturn(page);

        // Act
        clientService.findAllClientsByBusiness(0, 10, null, user);

        // Assert
        verify(clientRepository).findByBusinessIdOrderByClientName(eq(businessId), any(Pageable.class));
        verify(clientRepository, never()).findByBusinessIdAndClientNameContainingIgnoreCaseOrderByClientName(any(), any(), any());
    }

    @Test
    @DisplayName("Deve listar os clientes da empresa com filtro de busca")
    void findAllClientsByBusiness_WithSearch() {
        // Arrange
        String searchTerm = "Teste";
        Page<Client> page = new PageImpl<>(List.of(new Client()));
        when(clientRepository.findByBusinessIdAndClientNameContainingIgnoreCaseOrderByClientName(eq(businessId), eq(searchTerm), any(Pageable.class))).thenReturn(page);

        // Act
        clientService.findAllClientsByBusiness(0, 10, searchTerm, user);

        // Assert
        verify(clientRepository).findByBusinessIdAndClientNameContainingIgnoreCaseOrderByClientName(eq(businessId), eq(searchTerm), any(Pageable.class));
        verify(clientRepository, never()).findByBusinessIdOrderByClientName(any(), any());
    }
}
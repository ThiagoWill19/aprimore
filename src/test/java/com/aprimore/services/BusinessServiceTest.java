package com.aprimore.services;

import com.aprimore.events.BusinessCreatedEvent;
import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Business;
import com.aprimore.models.User;
import com.aprimore.models.dtos.BusinessDetailsDto;
import com.aprimore.models.dtos.NewBusinessDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.Role;
import com.aprimore.models.mappers.BusinessMapper;
import com.aprimore.repositories.BusinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private BusinessMapper businessMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BusinessService businessService;

    private NewBusinessDto newBusinessDto;

    @BeforeEach
    void setUp() {
        newBusinessDto = new NewBusinessDto();
        newBusinessDto.setBusinessName("Empresa Teste");
        newBusinessDto.setTradeName("Nome Fantasia");
        newBusinessDto.setCnpj("12.345.678/0001-99");
        newBusinessDto.setBusinessEmail("contato@empresa.com");
        newBusinessDto.setPhone("(11) 98765-4321");
        newBusinessDto.setUsername("Usuário Teste");
        newBusinessDto.setEmail("usuario@empresa.com");
    }

    @Test
    @DisplayName("Deve criar uma nova empresa e usuário com sucesso e publicar evento")
    void newBusiness_Success() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("senha_codificada");

        // Act
        businessService.newBusiness(newBusinessDto);

        // Assert
        ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
        verify(businessRepository).saveAndFlush(businessCaptor.capture());
        Business savedBusiness = businessCaptor.getValue();

        assertEquals("Empresa Teste", savedBusiness.getName());
        assertEquals("12345678000199", savedBusiness.getCnpj()); // Verifica se limpou a máscara
        assertEquals("11987654321", savedBusiness.getPhone()); // Verifica se limpou a máscara
        assertFalse(savedBusiness.getUsers().isEmpty());

        User savedUser = savedBusiness.getUsers().get(0);
        assertEquals("Usuário Teste", savedUser.getName());
        assertEquals("usuario@empresa.com", savedUser.getEmail());
        assertEquals(Role.USER, savedUser.getRole());
        assertEquals("senha_codificada", savedUser.getPassword());

        ArgumentCaptor<BusinessCreatedEvent> eventCaptor = ArgumentCaptor.forClass(BusinessCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(savedUser, eventCaptor.getValue().getUser());
        assertNotNull(eventCaptor.getValue().getRawPassword());
    }

    @Test
    @DisplayName("[Criação] Deve lançar DomainRuleException em caso de violação de integridade")
    void newBusiness_ShouldThrowDomainRuleException_OnDataIntegrityViolation() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("senha_codificada");
        when(businessRepository.saveAndFlush(any(Business.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> businessService.newBusiness(newBusinessDto));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve encontrar todas as empresas quando a busca é nula")
    void findAllByOrderByName_WithNullSearch() {
        // Arrange
        Page<Business> page = new PageImpl<>(List.of(new Business()));
        when(businessRepository.findAllByOrderByName(any(Pageable.class))).thenReturn(page);

        // Act
        businessService.findAllByOrderByName(0, 10, null);

        // Assert
        verify(businessRepository).findAllByOrderByName(any(Pageable.class));
        verify(businessRepository, never()).findByCnpjContaining(anyString(), any(Pageable.class));
        verify(businessRepository, never()).findByNameOrTradeName(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar por CNPJ quando a busca parece um CNPJ")
    void findAllByOrderByName_WithCnpjSearch() {
        // Arrange
        String search = "12.345.678/0001-99";
        String numericCnpj = "12345678000199";
        Page<Business> page = new PageImpl<>(List.of(new Business()));
        when(businessRepository.findByCnpjContaining(eq(numericCnpj), any(Pageable.class))).thenReturn(page);

        // Act
        businessService.findAllByOrderByName(0, 10, search);

        // Assert
        verify(businessRepository).findByCnpjContaining(eq(numericCnpj), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar por nome quando a busca é um texto")
    void findAllByOrderByName_WithNameSearch() {
        // Arrange
        String search = "Empresa";
        Page<Business> page = new PageImpl<>(List.of(new Business()));
        when(businessRepository.findByNameOrTradeName(eq(search.toLowerCase()), any(Pageable.class))).thenReturn(page);

        // Act
        businessService.findAllByOrderByName(0, 10, search);

        // Assert
        verify(businessRepository).findByNameOrTradeName(eq(search.toLowerCase()), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve encontrar detalhes da empresa por ID com sucesso")
    void findById_Success() {
        // Arrange
        UUID businessId = UUID.randomUUID();
        Business business = new Business();
        business.getUsers().add(new User());
        business.getUsers().add(new User());

        BusinessDetailsDto dto = new BusinessDetailsDto();

        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));
        when(businessMapper.mapToBusinessDetailsDto(business)).thenReturn(dto);

        // Act
        BusinessDetailsDto result = businessService.findById(businessId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuantityUser());
        assertEquals(0, result.getQuantityClients());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando a empresa não é encontrada por ID")
    void findById_ShouldThrowResourceNotFound() {
        // Arrange
        UUID businessId = UUID.randomUUID();
        when(businessRepository.findById(businessId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> businessService.findById(businessId));
    }

    @Test
    @DisplayName("Deve mudar o status de ATIVO para INATIVO")
    void changeBusinessStatus_FromActiveToInactive() {
        // Arrange
        UUID businessId = UUID.randomUUID();
        Business business = new Business();
        business.setAccountStatus(AccountStatus.ACTIVE);
        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));

        // Act
        businessService.changeBusinessStatus(businessId);

        // Assert
        ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);
        verify(businessRepository).save(captor.capture());
        assertEquals(AccountStatus.INACTIVE, captor.getValue().getAccountStatus());
    }
}
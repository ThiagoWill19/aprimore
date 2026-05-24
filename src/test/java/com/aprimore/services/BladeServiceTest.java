package com.aprimore.services;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Blade;
import com.aprimore.models.Business;
import com.aprimore.models.User;
import com.aprimore.models.dtos.BladeListDto;
import com.aprimore.models.dtos.NewBladeDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.BladeType;
import com.aprimore.models.mappers.BladeMapper;
import com.aprimore.repositories.BusinessRepository;
import com.aprimore.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BladeServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private BladeMapper bladeMapper;

    @InjectMocks
    private BladeService bladeService;

    private User user;
    private Business business;
    private UUID businessId;

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();

        business = new Business();
        business.setId(businessId);
        business.setAccountStatus(AccountStatus.ACTIVE);

        user = new User();
        user.setBusiness(business);
    }

    @Test
    @DisplayName("Deve listar todas as lâminas da empresa com sucesso")
    void findAllByBusiness_Success() {
        // Arrange
        Blade blade = new Blade();
        when(itemRepository.findBladesByBusinessId(businessId)).thenReturn(List.of(blade));
        when(bladeMapper.mapToBladeListDto(any(Blade.class))).thenReturn(new BladeListDto());

        // Act
        List<BladeListDto> result = bladeService.findAllByBusiness(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository).findBladesByBusinessId(businessId);
        verify(bladeMapper).mapToBladeListDto(blade);
    }

    @Test
    @DisplayName("Deve criar uma nova lâmina com sucesso")
    void createBlade_Success() {
        // Arrange
        NewBladeDto dto = new NewBladeDto();
        dto.setBladeType(BladeType.STRAIGHT_CUT);
        dto.setCutType("A");
        dto.setEspessure(2);
        dto.setHeight(23.8);
        dto.setDescription("Descrição");
        dto.setManufacturer("Fabricante");
        Blade blade = new Blade();
        BladeListDto expectedDto = new BladeListDto();

        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));
        when(itemRepository.existsBladeByBusinessIdAndName(eq(businessId), anyString())).thenReturn(false);
        when(bladeMapper.mapToBlade(dto, business)).thenReturn(blade);
        when(itemRepository.save(blade)).thenReturn(blade);
        when(bladeMapper.mapToBladeListDto(blade)).thenReturn(expectedDto);

        // Act
        BladeListDto result = bladeService.createBlade(dto, user);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(itemRepository).save(blade);
    }

    @Test
    @DisplayName("Criação - Deve lançar exceção se a empresa estiver inativa")
    void createBlade_ShouldThrowAccessDenied_WhenBusinessIsInactive() {
        // Arrange
        business.setAccountStatus(AccountStatus.INACTIVE);
        NewBladeDto dto = new NewBladeDto();
        dto.setBladeType(BladeType.STRAIGHT_CUT);
        dto.setCutType("A");
        dto.setEspessure(2);
        dto.setHeight(23.8);
        dto.setDescription("Descrição");
        dto.setManufacturer("Fabricante");
        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> bladeService.createBlade(dto, user));
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criação - Deve lançar exceção se a lâmina já existir")
    void createBlade_ShouldThrowDomainRuleException_WhenBladeExists() {
        // Arrange
        NewBladeDto dto = new NewBladeDto();
        dto.setBladeType(BladeType.STRAIGHT_CUT);
        dto.setCutType("A");
        dto.setEspessure(2);
        dto.setHeight(23.8);
        dto.setDescription("Descrição");
        dto.setManufacturer("Fabricante");
        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));
        when(itemRepository.existsBladeByBusinessIdAndName(eq(businessId), anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> bladeService.createBlade(dto, user));
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar uma lâmina com sucesso")
    void deleteBlade_Success() {
        // Arrange
        UUID bladeId = UUID.randomUUID();
        Blade blade = new Blade();
        blade.setBusiness(business); // Lâmina pertence à empresa do usuário

        when(itemRepository.findById(bladeId)).thenReturn(Optional.of(blade));

        // Act & Assert
        assertDoesNotThrow(() -> bladeService.deleteBlade(bladeId, user));

        // Verify
        verify(itemRepository).delete(blade);
    }

    @Test
    @DisplayName("Deleção - Deve lançar exceção se a lâmina não for encontrada")
    void deleteBlade_ShouldThrowResourceNotFound_WhenBladeNotFound() {
        // Arrange
        UUID bladeId = UUID.randomUUID();
        when(itemRepository.findById(bladeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bladeService.deleteBlade(bladeId, user));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deleção - Deve negar acesso se a lâmina pertencer a outra empresa")
    void deleteBlade_ShouldThrowAccessDenied_WhenBladeBelongsToAnotherBusiness() {
        // Arrange
        UUID bladeId = UUID.randomUUID();
        Blade blade = new Blade();
        Business anotherBusiness = new Business();
        anotherBusiness.setId(UUID.randomUUID());
        blade.setBusiness(anotherBusiness); // Lâmina de outra empresa

        when(itemRepository.findById(bladeId)).thenReturn(Optional.of(blade));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> bladeService.deleteBlade(bladeId, user));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deleção - Deve negar acesso se a empresa do usuário estiver inativa")
    void deleteBlade_ShouldThrowAccessDenied_WhenBusinessIsInactive() {
        // Arrange
        business.setAccountStatus(AccountStatus.INACTIVE);
        UUID bladeId = UUID.randomUUID();
        Blade blade = new Blade();
        blade.setBusiness(business);

        when(itemRepository.findById(bladeId)).thenReturn(Optional.of(blade));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> bladeService.deleteBlade(bladeId, user));
        verify(itemRepository, never()).delete(any());
    }
}

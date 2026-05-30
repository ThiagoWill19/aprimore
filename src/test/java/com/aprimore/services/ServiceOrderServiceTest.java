package com.aprimore.services;

import com.aprimore.exceptions.DomainRuleException;
import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.RotaryMachine;
import com.aprimore.models.*;
import com.aprimore.models.dtos.NewServiceOrderDto;
import com.aprimore.models.dtos.ServiceOrderDetailsDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.ServiceOrderStatus;
import com.aprimore.models.mappers.ServiceOrderMapper;
import com.aprimore.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private MachineRepository machineRepository;
    @Mock
    private BladeRepository bladeRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ServiceOrderMapper serviceOrderMapper;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    private User user;
    private Business business;
    private Client client;
    private Machine machine;
    private ServiceOrder serviceOrder;
    private NewServiceOrderDto newServiceOrderDto;
    private ServiceOrderDetailsDto serviceOrderDetailsDto;

    private UUID businessId;
    private UUID clientId;
    private UUID machineId;
    private Long serviceOrderId;

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        machineId = UUID.randomUUID();
        serviceOrderId = 1L;

        business = new Business();
        business.setId(businessId);
        business.setAccountStatus(AccountStatus.ACTIVE);

        user = new User();
        user.setBusiness(business);

        client = new Client();
        client.setId(clientId);
        client.setBusiness(business);

        machine = new RotaryMachine();
        machine.setId(machineId);
        machine.setClient(client);
        machine.setActive(true);
        machine.setWave("A, B, C");

        serviceOrder = new ServiceOrder();
        serviceOrder.setId(serviceOrderId);
        serviceOrder.setClient(client);
        serviceOrder.setMachine(machine);
        serviceOrder.setStatus(ServiceOrderStatus.OPEN);

        newServiceOrderDto = new NewServiceOrderDto();
        newServiceOrderDto.setMachineId(machineId);
        newServiceOrderDto.setEntryDate(LocalDate.now());
        newServiceOrderDto.setDeliveryDate(LocalDate.now().plusDays(5));
        newServiceOrderDto.setTypeOfWave("A");
    }

    // --- Testes para createServiceOrder ---

    @Test
    @DisplayName("Deve criar uma Ordem de Serviço com sucesso e calcular sequência e número")
    void createServiceOrder_Success() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));
        when(serviceOrderRepository.findMaxPcpSequenceByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN)).thenReturn(10);
        when(serviceOrderRepository.findMaxOrderNumberByBusiness(businessId)).thenReturn(Optional.of(100));

        ServiceOrder mappedOrder = new ServiceOrder();
        when(serviceOrderMapper.mapToNewEntity(any(), any(), any(), any(), any(), any())).thenReturn(mappedOrder);
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenReturn(mappedOrder);

        // Act
        serviceOrderService.createServiceOrder(clientId, newServiceOrderDto, user);

        // Assert
        ArgumentCaptor<ServiceOrder> captor = ArgumentCaptor.forClass(ServiceOrder.class);
        verify(serviceOrderRepository).save(captor.capture());

        ServiceOrder savedOrder = captor.getValue();
        assertEquals(11, savedOrder.getPcpSequence()); // 10 (max) + 1
        assertEquals(101, savedOrder.getOrderNumber()); // 100 (max) + 1
        assertEquals(ServiceOrderStatus.OPEN, savedOrder.getStatus());
    }

    @Test
    @DisplayName("[Criação] Deve lançar exceção se a máquina estiver inativa")
    void createServiceOrder_ShouldThrowDomainRuleException_WhenMachineIsInactive() {
        // Arrange
        machine.setActive(false);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> serviceOrderService.createServiceOrder(clientId, newServiceOrderDto, user));
    }

    @Test
    @DisplayName("[Criação] Deve lançar exceção se a máquina não pertencer ao cliente")
    void createServiceOrder_ShouldThrowDomainRuleException_WhenMachineDoesNotBelongToClient() {
        // Arrange
        Client anotherClient = new Client(); // Simula uma máquina de outro cliente
        anotherClient.setId(UUID.randomUUID());
        machine.setClient(anotherClient);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> serviceOrderService.createServiceOrder(clientId, newServiceOrderDto, user));
    }

    @Test
    @DisplayName("[Criação] Deve lançar exceção se o tipo de onda for inválido para a máquina")
    void createServiceOrder_ShouldThrowDomainRuleException_WhenWaveIsInvalid() {
        // Arrange
        newServiceOrderDto.setTypeOfWave("Z"); // Onda inválida
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> serviceOrderService.createServiceOrder(clientId, newServiceOrderDto, user));
    }

    @Test
    @DisplayName("[Criação] Deve lançar exceção se a data de entrega for anterior à de entrada")
    void createServiceOrder_ShouldThrowDomainRuleException_WhenDeliveryDateIsBeforeEntryDate() {
        // Arrange
        newServiceOrderDto.setDeliveryDate(LocalDate.now().minusDays(1));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> serviceOrderService.createServiceOrder(clientId, newServiceOrderDto, user));
    }

    @Test
    @DisplayName("[Criação] Deve lançar exceção se uma lâmina não for encontrada")
    void createServiceOrder_ShouldThrowResourceNotFound_WhenBladeNotFound() {
        // Arrange
        UUID bladeId = UUID.randomUUID();
        newServiceOrderDto.setBladeIds(List.of(bladeId));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));
        when(bladeRepository.findAllById(List.of(bladeId))).thenReturn(new ArrayList<>()); // Retorna lista vazia

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> serviceOrderService.createServiceOrder(clientId, newServiceOrderDto, user));
    }

    // --- Testes para updateServiceOrder ---

    @Test
    @DisplayName("Deve atualizar uma Ordem de Serviço com sucesso")
    void updateServiceOrder_Success() {
        // Arrange
        serviceOrderDetailsDto = new ServiceOrderDetailsDto();
        serviceOrderDetailsDto.setMachineId(machineId);
        serviceOrderDetailsDto.setTypeOfWave("A");
        serviceOrderDetailsDto.setStatus(ServiceOrderStatus.OPEN);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(serviceOrderRepository.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));

        // Act
        serviceOrderService.updateServiceOrder(clientId, serviceOrderId, serviceOrderDetailsDto, user);

        // Assert
        verify(serviceOrderMapper).updateEntityFromDetailsDto(eq(serviceOrderDetailsDto), eq(serviceOrder), eq(machine), any(), any());
        verify(serviceOrderRepository).save(serviceOrder);
        assertNull(serviceOrder.getPcpSequence()); // Status não mudou para OPEN, então não recalcula
    }

    @Test
    @DisplayName("[Atualização] Deve recalcular PCP se status mudar para OPEN")
    void updateServiceOrder_ShouldRecalculatePcpSequence_WhenStatusChangesToOpen() {
        // Arrange
        serviceOrder.setStatus(ServiceOrderStatus.CLOSED); // Status anterior era CLOSED
        serviceOrderDetailsDto = new ServiceOrderDetailsDto();
        serviceOrderDetailsDto.setMachineId(machineId);
        serviceOrderDetailsDto.setTypeOfWave("A");
        serviceOrderDetailsDto.setStatus(ServiceOrderStatus.OPEN); // Novo status é OPEN

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(serviceOrderRepository.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));
        when(serviceOrderRepository.findMaxPcpSequenceByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN)).thenReturn(5);

        // Simula a ação do mapper, que é atualizar o status da entidade.
        doAnswer(invocation -> {
            ServiceOrder orderToUpdate = invocation.getArgument(1);
            orderToUpdate.setStatus(ServiceOrderStatus.OPEN);
            return null; // para métodos void
        }).when(serviceOrderMapper).updateEntityFromDetailsDto(any(), any(), any(), any(), any());

        // Act
        serviceOrderService.updateServiceOrder(clientId, serviceOrderId, serviceOrderDetailsDto, user);

        // Assert
        ArgumentCaptor<ServiceOrder> captor = ArgumentCaptor.forClass(ServiceOrder.class);
        verify(serviceOrderRepository).save(captor.capture());
        assertEquals(6, captor.getValue().getPcpSequence()); // 5 (max) + 1
    }

    // --- Testes para listAllByBusiness ---

    @Test
    @DisplayName("Deve listar OS da empresa sem filtros")
    void listAllByBusiness_NoFilters() {
        // Arrange
        Page<ServiceOrder> page = new PageImpl<>(List.of(new ServiceOrder()));
        when(serviceOrderRepository.findAllByBusinessWithFilters(eq(businessId), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        // Act
        serviceOrderService.listAllByBusiness(0, 10, user);

        // Assert
        verify(serviceOrderRepository).findAllByBusinessWithFilters(eq(businessId), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
        verify(serviceOrderMapper, times(1)).mapToListDto(any(ServiceOrder.class));
    }

    @Test
    @DisplayName("Deve listar OS da empresa com filtro de busca por número")
    void listAllByBusiness_WithNumericSearch() {
        // Arrange
        String search = "123";
        Page<ServiceOrder> page = new PageImpl<>(List.of(new ServiceOrder()));
        when(serviceOrderRepository.findAllByBusinessWithFilters(eq(businessId), eq(search), eq(123), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        // Act
        serviceOrderService.listAllByBusiness(0, 10, search, null, null, user);

        // Assert
        verify(serviceOrderRepository).findAllByBusinessWithFilters(eq(businessId), eq(search), eq(123), isNull(), isNull(), any(Pageable.class));
    }

    // --- Testes para findById ---

    @Test
    @DisplayName("Deve encontrar detalhes da OS por ID com sucesso")
    void findById_Success() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(serviceOrderRepository.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(serviceOrderMapper.mapToDetailsDto(serviceOrder)).thenReturn(new ServiceOrderDetailsDto());

        // Act
        ServiceOrderDetailsDto result = serviceOrderService.findById(clientId, serviceOrderId, user);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("[Busca] Deve negar acesso se a OS não pertencer ao cliente informado")
    void findById_ShouldThrowAccessDenied_WhenServiceOrderDoesNotBelongToClient() {
        // Arrange
        Client anotherClient = new Client();
        anotherClient.setId(UUID.randomUUID());
        serviceOrder.setClient(anotherClient); // OS pertence a outro cliente

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(serviceOrderRepository.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> serviceOrderService.findById(clientId, serviceOrderId, user));
    }

    @Test
    @DisplayName("[Busca] Deve lançar exceção se a OS não for encontrada")
    void findById_ShouldThrowResourceNotFound_WhenServiceOrderNotFound() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(serviceOrderRepository.findById(serviceOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> serviceOrderService.findById(clientId, serviceOrderId, user));
    }
}
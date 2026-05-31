package com.aprimore.services;

import com.aprimore.exceptions.ResourceNotFoundException;
import com.aprimore.models.Business;
import com.aprimore.models.Client;
import com.aprimore.models.Machine;
import com.aprimore.models.RotaryMachine;
import com.aprimore.models.User;
import com.aprimore.models.dtos.MachineDetailsDto;
import com.aprimore.models.dtos.NewMachineDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.mappers.MachineMapper;
import com.aprimore.repositories.ClientRepository;
import com.aprimore.repositories.MachineRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineServiceTest {

    @Mock
    private MachineRepository machineRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private MachineMapper machineMapper;

    @InjectMocks
    private MachineService machineService;

    private User user;
    private Business business;
    private Client client;
    private Machine machine;

    private UUID businessId;
    private UUID clientId;
    private UUID machineId;

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        machineId = UUID.randomUUID();

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
    }

    @Test
    @DisplayName("Deve listar as máquinas de um cliente com sucesso")
    void listByClient_Success() {
        // Arrange
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        Page<Machine> page = new PageImpl<>(List.of(machine));
        when(machineRepository.findByClientIdOrderByActiveDescNameAsc(eq(clientId), any(Pageable.class))).thenReturn(page);

        // Act
        machineService.listByClient(clientId, 0, 10, user);

        // Assert
        verify(machineRepository).findByClientIdOrderByActiveDescNameAsc(eq(clientId), any(Pageable.class));
        verify(machineMapper).mapToListDto(machine);
    }

    @Test
    @DisplayName("Deve criar uma nova máquina com sucesso")
    void createMachine_Success() {
        // Arrange
        NewMachineDto dto = new NewMachineDto();
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(machineMapper.mapToMachine(dto)).thenReturn(machine);

        // Act
        machineService.createMachine(clientId, dto, user);

        // Assert
        ArgumentCaptor<Machine> captor = ArgumentCaptor.forClass(Machine.class);
        verify(machineRepository).save(captor.capture());
        assertEquals(client, captor.getValue().getClient());
    }

    @Test
    @DisplayName("Deve atualizar o status de uma máquina de ativo para inativo")
    void updateMachineStatus_FromActiveToInactive() {
        // Arrange
        assertTrue(machine.isActive());
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));

        // Act
        machineService.updateMachineStatus(clientId, machineId, user);

        // Assert
        ArgumentCaptor<Machine> captor = ArgumentCaptor.forClass(Machine.class);
        verify(machineRepository).save(captor.capture());
        assertFalse(captor.getValue().isActive());
    }

    @Test
    @DisplayName("Deve encontrar detalhes da máquina por ID com sucesso")
    void findById_Success() {
        // Arrange
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));
        when(machineMapper.mapToDetailsDto(machine)).thenReturn(new MachineDetailsDto());

        // Act
        MachineDetailsDto result = machineService.findById(clientId, machineId, user);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve atualizar os dados de uma máquina com sucesso")
    void updateMachine_Success() {
        // Arrange
        MachineDetailsDto dto = new MachineDetailsDto();
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));

        // Act
        machineService.updateMachine(clientId, machineId, dto, user);

        // Assert
        verify(machineMapper).updateMachineFromDetailsDto(dto, machine);
        verify(machineRepository).save(machine);
    }

    @Test
    @DisplayName("[Segurança] Deve negar acesso se o cliente não pertencer à empresa do usuário")
    void shouldThrowAccessDenied_WhenClientBelongsToAnotherBusiness() {
        // Arrange
        Business anotherBusiness = new Business();
        anotherBusiness.setId(UUID.randomUUID());
        client.setBusiness(anotherBusiness); // Cliente de outra empresa

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> machineService.listByClient(clientId, 0, 10, user));
    }

    @Test
    @DisplayName("[Segurança] Deve negar acesso se a máquina não pertencer ao cliente informado")
    void shouldThrowAccessDenied_WhenMachineBelongsToAnotherClient() {
        // Arrange
        UUID anotherClientId = UUID.randomUUID(); // ID de cliente diferente na URL
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> machineService.findById(anotherClientId, machineId, user));
    }
}
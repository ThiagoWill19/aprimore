package com.aprimore.services;

import com.aprimore.models.Business;
import com.aprimore.models.Client;
import com.aprimore.models.ServiceOrder;
import com.aprimore.models.User;
import com.aprimore.models.dtos.DashboardDto;
import com.aprimore.models.enuns.AccountStatus;
import com.aprimore.models.enuns.ServiceOrderStatus;
import com.aprimore.repositories.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private DashboardService dashboardService;

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
    @DisplayName("Deve carregar o dashboard com sucesso e gerar alertas para OS atrasadas")
    void loadDashboard_Success_WithAlerts() {
        // Arrange
        Client client = new Client();
        client.setClientName("Cliente A");

        ServiceOrder overdueOrder = new ServiceOrder();
        overdueOrder.setId(1L);
        overdueOrder.setOrderNumber(100);
        overdueOrder.setClient(client);
        overdueOrder.setDeliveryDate(LocalDate.now().minusDays(1)); // Atrasada

        when(serviceOrderRepository.countByClientBusinessIdAndStatus(businessId, ServiceOrderStatus.OPEN)).thenReturn(5L);
        when(serviceOrderRepository.countByClientBusinessIdAndStatus(businessId, ServiceOrderStatus.CLOSED)).thenReturn(10L);
        when(serviceOrderRepository.countByClientBusinessIdAndStatusAndDeliveryDateBefore(eq(businessId), eq(ServiceOrderStatus.OPEN), any(LocalDate.class))).thenReturn(1L);
        when(serviceOrderRepository.findPriorityByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN)).thenReturn(List.of(overdueOrder));

        // Act
        DashboardDto result = dashboardService.loadDashboard(user);

        // Assert
        assertEquals(5L, result.getOpenServiceOrders());
        assertEquals(10L, result.getClosedServiceOrders());
        assertEquals(1L, result.getOverdueServiceOrders());
        assertFalse(result.getPriorityServiceOrders().isEmpty());
        assertTrue(result.getPriorityServiceOrders().get(0).isOverdue());
        assertFalse(result.getAlerts().isEmpty());
        assertEquals("1 OS aberta(s) com prazo vencido.", result.getAlerts().get(0));
    }

    @Test
    @DisplayName("[Dashboard] Deve negar acesso se a empresa estiver inativa")
    void loadDashboard_ShouldThrowAccessDenied_WhenBusinessIsInactive() {
        // Arrange
        business.setAccountStatus(AccountStatus.INACTIVE);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> dashboardService.loadDashboard(user));
    }

    @Test
    @DisplayName("Deve atualizar a sequência do PCP com sucesso e reordenar as restantes")
    void updatePcpSequence_Success_WithRemainingOrders() {
        // Arrange
        List<Long> orderedIds = List.of(3L, 1L); // Priorizadas pelo usuário

        ServiceOrder so1 = new ServiceOrder(); so1.setId(1L);
        ServiceOrder so2 = new ServiceOrder(); so2.setId(2L); // Não priorizada
        ServiceOrder so3 = new ServiceOrder(); so3.setId(3L);
        ServiceOrder so4 = new ServiceOrder(); so4.setId(4L); // Não priorizada

        List<ServiceOrder> requestedOrders = List.of(so1, so3);
        List<ServiceOrder> allOpenOrders = List.of(so1, so2, so3, so4);

        when(serviceOrderRepository.findByBusinessAndStatusAndIdIn(businessId, ServiceOrderStatus.OPEN, orderedIds))
                .thenReturn(requestedOrders);
        when(serviceOrderRepository.findPriorityByBusinessAndStatus(businessId, ServiceOrderStatus.OPEN))
                .thenReturn(allOpenOrders);

        // Act
        dashboardService.updatePcpSequence(orderedIds, user);

        // Assert
        assertEquals(2, so1.getPcpSequence()); // O segundo na lista `orderedIds`
        assertEquals(1, so3.getPcpSequence()); // O primeiro na lista `orderedIds`
        assertEquals(3, so2.getPcpSequence()); // A primeira não priorizada, continua a sequência
        assertEquals(4, so4.getPcpSequence()); // A segunda não priorizada
    }

    @Test
    @DisplayName("[PCP] Deve lançar exceção se a sequência de IDs for inválida")
    void updatePcpSequence_ShouldThrowAccessDenied_WhenSequenceIsInvalid() {
        // Arrange
        List<Long> orderedIds = List.of(1L, 99L); // 99L não existe ou não pertence à empresa
        ServiceOrder so1 = new ServiceOrder(); so1.setId(1L);

        // O repositório retorna apenas 1 OS, mas foram pedidos 2
        when(serviceOrderRepository.findByBusinessAndStatusAndIdIn(businessId, ServiceOrderStatus.OPEN, orderedIds))
                .thenReturn(List.of(so1));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> dashboardService.updatePcpSequence(orderedIds, user));
    }

    @Test
    @DisplayName("[PCP] Não deve fazer nada se a lista de IDs for nula ou vazia")
    void updatePcpSequence_ShouldDoNothing_ForNullOrEmptyList() {
        // Act
        dashboardService.updatePcpSequence(null, user);
        dashboardService.updatePcpSequence(new ArrayList<>(), user);

        // Assert
        verify(serviceOrderRepository, never()).findByBusinessAndStatusAndIdIn(any(), any(), any());
    }
}
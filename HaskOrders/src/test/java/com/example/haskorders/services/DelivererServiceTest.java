package com.example.haskorders.services;

import com.example.haskorders.entities.DelivererBonus;
import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.order.Order_status;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.DelivererBonusRepository;
import com.example.haskorders.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DelivererServiceTest {
    private OrderRepository orderRepository;
    private OrderService orderService;
    private DelivererBonusRepository delivererBonusRepository;
    private DelivererService delivererService;

    @BeforeEach
    public void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = Mockito.mock(OrderService.class);
        delivererBonusRepository = Mockito.mock(DelivererBonusRepository.class);
        delivererService = new DelivererService(orderRepository, orderService, delivererBonusRepository);
    }

    @Test
    public void getAvailableOrders_ReturnsReadyOrders() {
        Order order = new Order();
        when(orderRepository.findByStatus(Order_status.READY)).thenReturn(List.of(order));

        List<Order> orders = delivererService.getAvailableDeliveries();
        assertEquals(1, orders.size());
    }

    @Test
    public void calculateDelivererRevenue_SumsOrderTotals() {
        User deliverer = new User();
        Order order = new Order();
        Order order2 = new Order();
        when(orderRepository.findByDelivererAndStatusAndDateBetween(any(), eq(Order_status.DELIVERED), any(), any())).thenReturn(List.of(order, order2));
        when(orderService.calculateOrderTotal(order)).thenAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                if (o == order) return 50.0;
                else if (o == order2) return 30.0;
                else return 0.0;
            });

        double revenue = delivererService.calculateDelivererRevenue(deliverer, LocalDateTime.now(), LocalDateTime.now());

        assertEquals(80.0, revenue);
    }

    @Test
    public void assignDeliverer_OrderExists_AssignsSuccessfully() {
        User deliverer = new User();
        Order order = new Order();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        boolean result = delivererService.assignDeliverer(1, deliverer);

        assertTrue(result);
        assertEquals(Order_status.SENT, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    public void assignDeliverer_OrderNotFound_ReturnsFalse() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        boolean result = delivererService.assignDeliverer(1, new User());

        assertFalse(result);
    }

    @Test
    public void addOrderTotalToRevenue_UpdatesTotalRevenue() {
        User deliverer = new User();
        Order order = new Order();
        DelivererBonus bonus = new DelivererBonus(100.0, 0, deliverer);

        when(delivererBonusRepository.findByDeliverer(deliverer)).thenReturn(Optional.of(bonus));
        when(orderService.calculateOrderTotal(order)).thenReturn(50.0);

        delivererService.addOrderTotalToRevenue(deliverer, order);

        assertEquals(150.0, bonus.getTotalRevenue());
        verify(delivererBonusRepository).save(bonus);
    }

    @Test
    public void checkAndApplyBonus_UnlocksBonus() {
        User deliverer = new User();
        DelivererBonus bonus = new DelivererBonus(90.0, 0, deliverer);

        when(delivererBonusRepository.findByDeliverer(deliverer)).thenReturn(Optional.of(bonus));

        boolean unlocked = delivererService.checkAndApplyBonus(deliverer, 50.0);

        assertTrue(unlocked);
        assertEquals(1, bonus.getBonus_count());
        verify(delivererBonusRepository).save(bonus);
    }

    @Test
    public void getAssignedDeliveries_ReturnsSentOrders() {
        User deliverer = new User();
        when(orderRepository.findByDelivererAndStatus(deliverer, Order_status.SENT)).thenReturn(Collections.singletonList(new Order()));

        List<Order> orders = delivererService.getAssignedDeliveries(deliverer);

        assertEquals(1, orders.size());
    }

    @Test
    public void getCompletedDeliveries_ReturnsSentOrders() {
        User deliverer = new User();
        when(orderRepository.findByDelivererAndStatus(deliverer, Order_status.DELIVERED)).thenReturn(Collections.singletonList(new Order()));

        List<Order> orders = delivererService.getCompletedDeliveries(deliverer);

        assertEquals(1, orders.size());
    }

    @Test
    public void completeDelivery_UpdatesOrderStatus() {
        when(orderService.updateOrderStatus(1, Order_status.DELIVERED)).thenReturn(true);

        boolean result = delivererService.completeDelivery(1);

        assertTrue(result);
    }
}

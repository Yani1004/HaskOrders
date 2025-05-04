package com.example.haskorders.services;

import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.order.Order_status;
import com.example.haskorders.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    private OrderRepository orderRepository;
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderService(orderRepository);
    }

    @Test
    public void GetAllOrders() {
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(mockOrders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void AddOrder() {
        Order order = new Order();
        orderService.addOrder(order);

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void UpdateOrderStatus_success() {
        Order order = new Order();
        order.setId(1);
        order.setStatus(Order_status.PROCESSING);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        boolean result = orderService.updateOrderStatus(1, Order_status.DELIVERED);

        assertTrue(result);
        assertEquals(Order_status.DELIVERED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    public void UpdateOrderStatus_orderNotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        boolean result = orderService.updateOrderStatus(1, Order_status.DELIVERED);

        assertFalse(result);
        verify(orderRepository, never()).save(any());
    }

    @Test
    public void CalculateOrderTotal() {
        Order order = new Order();
        order.setTotal_price(50.0);

        double total = orderService.calculateOrderTotal(order);

        assertEquals(50.0, total);
    }

    @Test
    public void CalculateCompanyRevenue() {
        Order order1 = new Order();
        order1.setDate(LocalDateTime.now().minusDays(1));
        order1.setTotal_price(100.0);

        Order order2 = new Order();
        order2.setDate(LocalDateTime.now().minusDays(10));
        order2.setTotal_price(200.0);

        Order order3 = new Order(); // Outside range
        order3.setDate(LocalDateTime.now().minusDays(40));
        order3.setTotal_price(300.0);

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2, order3));

        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        double revenue = orderService.calculateCompanyRevenue(start, end);

        assertEquals(300.0, revenue, 0.001);
    }
}

package com.example.haskorders.services;

import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.order.Order_status;
import com.example.haskorders.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor()
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void addOrder(Order order) {
        orderRepository.save(order);
    }

    public boolean updateOrderStatus(int order_id, Order_status status) {
        Optional<Order> optionalOrder = orderRepository.findById(order_id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public double calculateCompanyRevenue(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findAll().stream()
                .filter(o -> !o.getDate().isBefore(start) && !o.getDate().isAfter(end))
                .mapToDouble(this::calculateOrderTotal)
                .sum();
    }

    public double calculateOrderTotal(Order order) {
        return order.getTotal_price();
    }
}

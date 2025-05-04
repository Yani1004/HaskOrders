package com.example.haskorders.services;

import com.example.haskorders.entities.DelivererBonus;
import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.order.Order_status;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.DelivererBonusRepository;
import com.example.haskorders.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DelivererService {
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final DelivererBonusRepository delivererBonusRepository;

    public List<Order> getAvailableDeliveries() {
        return orderRepository.findByStatus(Order_status.READY);
    }

    public double calculateDelivererRevenue(User deliverer, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByDelivererAndStatusAndDateBetween(deliverer, Order_status.DELIVERED, start, end);
        return orders.stream().mapToDouble(orderService::calculateOrderTotal).sum();
    }

    public boolean assignDeliverer(int order_id, User deliverer) {
        Optional<Order> optionalOrder = orderRepository.findById(order_id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setDeliverer(deliverer);
            order.setStatus(Order_status.SENT);
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public void addOrderTotalToRevenue(User deliverer, Order order) {
        double orderTotal = orderService.calculateOrderTotal(order);

        DelivererBonus delivererBonus = delivererBonusRepository.findByDeliverer(deliverer)
                .orElseGet(() -> new DelivererBonus(0.0, 0, deliverer));

        double newTotalRevenue = delivererBonus.getTotalRevenue() + orderTotal;
        delivererBonus.setTotalRevenue(newTotalRevenue);
        delivererBonusRepository.save(delivererBonus);
    }

    public boolean checkAndApplyBonus(User deliverer, double treshold) {
        DelivererBonus delivererBonus = delivererBonusRepository.findByDeliverer(deliverer)
                .orElseGet(() -> { DelivererBonus newBonus = new DelivererBonus(0.0, 0, deliverer);
                delivererBonusRepository.save(newBonus);
                return newBonus;
                });
        int previousBonusCount = delivererBonus.getBonus_count();
        int newBonusCount = (int) (delivererBonus.getTotalRevenue() / treshold);

        boolean bonusUnlocked = newBonusCount > previousBonusCount;

        if (bonusUnlocked) {
            delivererBonus.setBonus_count(newBonusCount);
            delivererBonusRepository.save(delivererBonus);
        }

        return bonusUnlocked;
    }

    public List<Order> getAssignedDeliveries(User deliverer) {
        return orderRepository.findByDelivererAndStatus(deliverer, Order_status.SENT);
    }

    public List<Order> getCompletedDeliveries(User deliverer) {
        return orderRepository.findByDelivererAndStatus(deliverer, Order_status.DELIVERED);
    }

    public boolean completeDelivery(int order_id) {
        return orderService.updateOrderStatus(order_id, Order_status.DELIVERED);
    }


}

package com.example.haskorders.client.client;

import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.services.OrderService;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

public class OrderStatusPage extends JFrame {
    private final User user;
    private final OrderService orderService;

    public OrderStatusPage(User user, OrderService orderService) {
        this.user = user;
        this.orderService = orderService;

        initialize();
    }

    private void initialize() {
        setTitle("Order Status");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Your Order Status", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        Order latestOrder = orderService.getAllOrders().stream()
                .filter(order -> order.getCustomer().getId() == user.getId())
                .max(Comparator.comparing(Order::getDate))
                .orElse(null);

        String statusText = (latestOrder != null)
                ? "Status: " + latestOrder.getStatus().name()
                : "No orders found.";

        JLabel statusLabel = new JLabel(statusText, SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(statusLabel, BorderLayout.CENTER);

        setVisible(true);
    }
}

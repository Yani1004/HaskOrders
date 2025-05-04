package com.example.haskorders.client.client;

import com.example.haskorders.client.cart.Cart;
import com.example.haskorders.client.cart.CartItem;
import com.example.haskorders.entities.Product;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.services.OrderService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.util.List;

public class CartPage extends JFrame {
    private final Cart cart;
    private final Runnable onCartUpdated;
    private final OrderService orderService;
    private final Restaurant restaurant;

    public CartPage(Cart cart, Runnable onCartUpdated, OrderService orderService, Restaurant restaurant) {
        this.cart = cart;
        this.onCartUpdated = onCartUpdated;
        this.orderService = orderService;
        this.restaurant = restaurant;
        initialize();
    }

    private void initialize() {
        setTitle("Your Cart");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Your Cart", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<CartItem> items = cart.getItems();
        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            itemsPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < items.size(); i++) {
                itemsPanel.add(createCartItemPanel(items.get(i), i));
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total: $" + String.format("%.2f", cart.getTotalPrice()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton finishOrderButton = new JButton("Finish order");
        finishOrderButton.addActionListener(this::checkoutAction);

        JButton clearButton = new JButton("Clear Cart");
        clearButton.addActionListener(this::clearCartAction);

        buttonPanel.add(clearButton);
        buttonPanel.add(finishOrderButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private JPanel createCartItemPanel(CartItem item, int index) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        itemPanel.setBackground(Color.WHITE);

        Product product = item.getProduct();
        JLabel nameLabel = new JLabel(product.getName() + " - $" + product.getPrice() +
                " x " + item.getQuantity() + " = $" + item.getTotalPrice());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JButton decreaseBtn = new JButton("-");
        decreaseBtn.addActionListener(e -> {
            if (item.getQuantity() > 1) {
                cart.updateQuantity(index, item.getQuantity() - 1);
            } else {
                cart.removeItem(index);
            }
            if (onCartUpdated != null) onCartUpdated.run();
            refreshCart();
        });

        JLabel quantityLabel = new JLabel(String.valueOf(item.getQuantity()));

        JButton increaseBtn = new JButton("+");
        increaseBtn.addActionListener(e -> {
            cart.updateQuantity(index, item.getQuantity() + 1);
            if (onCartUpdated != null) onCartUpdated.run();
            refreshCart();
        });

        quantityPanel.add(decreaseBtn);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(increaseBtn);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            cart.removeItem(index);
            if (onCartUpdated != null) onCartUpdated.run();
            refreshCart();
        });

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(quantityPanel, BorderLayout.CENTER);
        rightPanel.add(removeButton, BorderLayout.EAST);

        itemPanel.add(nameLabel, BorderLayout.CENTER);
        itemPanel.add(rightPanel, BorderLayout.EAST);
        return itemPanel;
    }

    private void refreshCart() {
        this.dispose();
        new CartPage(cart, onCartUpdated, orderService, restaurant);
    }

    private void checkoutAction(ActionEvent e) {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        new PaymentPage(cart, () -> {
            if (onCartUpdated != null) {
                onCartUpdated.run(); }
            this.dispose();
        }, orderService, restaurant);

    }

    private void clearCartAction(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear your cart?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            cart.clear();
            if (onCartUpdated != null) {
                onCartUpdated.run();
            }
            this.dispose();
        }
    }
}

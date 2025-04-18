package bg.haskorders.delivery.client;

import bg.haskorders.delivery.model.cart.Cart;
import bg.haskorders.delivery.model.Product;
import bg.haskorders.delivery.model.cart.CartItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CartPage extends JFrame {
    private final Cart cart;

    public CartPage(Cart cart) {
        this.cart = cart;
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

        if (cart.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            itemsPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < cart.getItems().size(); i++) {
                CartItem item = cart.getItems().get(i);
                itemsPanel.add(createCartItemPanel(item, i));
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Total price panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total: $" + String.format("%.2f", cart.getTotalPrice()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(this::checkoutAction);

        JButton clearButton = new JButton("Clear Cart");
        clearButton.addActionListener(this::clearCartAction);

        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);
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

        // Panel for quantity controls
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JButton decreaseBtn = new JButton("-");
        decreaseBtn.addActionListener(e -> {
            if (item.getQuantity() > 1) {
                cart.updateQuantity(index, item.getQuantity() - 1);
                refreshCart();
            } else {
                cart.removeItem(index);
                refreshCart();
            }
        });

        JLabel quantityLabel = new JLabel(String.valueOf(item.getQuantity()));

        JButton increaseBtn = new JButton("+");
        increaseBtn.addActionListener(e -> {
            cart.updateQuantity(index, item.getQuantity() + 1);
            refreshCart();
        });

        quantityPanel.add(decreaseBtn);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(increaseBtn);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            cart.removeItem(index);
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
        new CartPage(cart);
    }

    private void checkoutAction(ActionEvent e) {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
        } else {
            this.dispose();
            new PaymentPage(cart);
        }
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
            refreshCart();
        }
    }
}
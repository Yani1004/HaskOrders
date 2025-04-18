package bg.haskorders.delivery.client;

import javax.swing.*;
import java.awt.*;

public class OrderStatusPage extends JFrame {
    public OrderStatusPage() {
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

        JLabel statusLabel = new JLabel("Status: Processing", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(statusLabel, BorderLayout.CENTER);

        setVisible(true);
    }
}

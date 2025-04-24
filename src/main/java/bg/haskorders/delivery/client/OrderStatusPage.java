package bg.haskorders.delivery.client;

import bg.haskorders.delivery.model.order.Order;
import bg.haskorders.delivery.model.user.User;
import bg.haskorders.delivery.repository.OrderRepository;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

public class OrderStatusPage extends JFrame {
    private final User user;

    public OrderStatusPage(User user) {
        this.user = user;
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

        Order latestOrder = OrderRepository.getInstance().getAllOrders().stream()
                .filter(order -> order.getUser_id() == user.getUserId())
                .max(Comparator.comparing(Order::getOrder_date))
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

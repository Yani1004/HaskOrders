package bg.haskorders.delivery.deliverer;

import bg.haskorders.delivery.model.order.Order;
import bg.haskorders.delivery.model.user.User;
import bg.haskorders.delivery.deliverer.DelivererService;
import bg.haskorders.delivery.repository.OrderRepository;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class DelivererDashboard extends JFrame {

    private final User deliverer;
    private final DelivererService deliveryService;
    private final JTabbedPane tabbedPane;
    private final JPanel availableOrdersPanel;
    private final JPanel myOrdersPanel;

    public DelivererDashboard(User deliverer, OrderRepository orderRepository) {
        this.deliverer = deliverer;
        this.deliveryService = new DelivererService(orderRepository);

        setTitle("Deliverer Dashboard - " + deliverer.getUsername());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        availableOrdersPanel = new JPanel();
        myOrdersPanel = new JPanel();

        tabbedPane.addTab("Available Orders", availableOrdersPanel);
        tabbedPane.addTab("My Deliveries", myOrdersPanel);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTabs());

        JButton earningsBtn = new JButton("Show Earnings");
        earningsBtn.addActionListener(e -> showEarnings());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshBtn);
        topPanel.add(earningsBtn);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        refreshTabs();
        setVisible(true);
    }

    private void refreshTabs() {
        displayAvailableOrders();
        displayMyOrders();
    }

    private void displayAvailableOrders() {
        availableOrdersPanel.removeAll();
        availableOrdersPanel.setLayout(new BoxLayout(availableOrdersPanel, BoxLayout.Y_AXIS));
        List<Order> orders = deliveryService.getAvailableOrders();

        for (Order order : orders) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("Order #" + order.getOrder_id() + " | Address: " + order.getDelivery_address()));
            JButton acceptBtn = new JButton("Accept");
            acceptBtn.addActionListener(e -> {
                boolean accepted = deliveryService.takeOrder(order.getOrder_id(), deliverer.getUserId().intValue());
                if (accepted) {
                    JOptionPane.showMessageDialog(this, "Order accepted!");
                    refreshTabs();
                } else {
                    JOptionPane.showMessageDialog(this, "Order was taken by someone else.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            });
            panel.add(acceptBtn);
            availableOrdersPanel.add(panel);
        }

        availableOrdersPanel.revalidate();
        availableOrdersPanel.repaint();
    }

    private void displayMyOrders() {
        myOrdersPanel.removeAll();
        myOrdersPanel.setLayout(new BoxLayout(myOrdersPanel, BoxLayout.Y_AXIS));
        List<Order> orders = deliveryService.getAssignedOrders(deliverer.getUserId().intValue());

        for (Order order : orders) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("Order #" + order.getOrder_id() + " | Address: " + order.getDelivery_address()));

            JButton completeBtn = new JButton("Complete Delivery");
            completeBtn.addActionListener(e -> {
                boolean completed = deliveryService.completeDelivery(order.getOrder_id());
                if (completed) {
                    JOptionPane.showMessageDialog(this, "Delivery marked as complete.");
                    refreshTabs();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not complete delivery.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(completeBtn);
            myOrdersPanel.add(panel);
        }

        myOrdersPanel.revalidate();
        myOrdersPanel.repaint();
    }


    private void showEarnings() {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        double threshold = 500.0; // може да е configurable от settings

        double earnings = deliveryService.getEarnings(deliverer.getUserId().intValue(), start, end);
        boolean hasBonus = deliveryService.hasBonus(deliverer.getUserId().intValue(), start, end, threshold);

        JOptionPane.showMessageDialog(this,
                "Earnings (last 30 days): $" + String.format("%.2f", earnings) +
                        "\nBonus eligible: " + (hasBonus ? "YES 🎉" : "NO"),
                "Earnings Summary",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

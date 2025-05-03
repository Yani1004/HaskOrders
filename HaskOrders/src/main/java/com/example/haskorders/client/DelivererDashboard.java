package com.example.haskorders.client;

import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.services.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;


public class DelivererDashboard extends JFrame {
    private final User deliverer;
    private final DelivererService delivererService;
    private final AdminService adminService;
    private final RegistrationService registrationService;
    private final AuthService authService;
    private final EmployeeService employeeService;
    private final OrderService orderService;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JPanel availableOrdersPanel = new JPanel();
    private final JPanel myOrdersPanel = new JPanel();

    public DelivererDashboard(User deliverer, DelivererService delivererService,
                              AdminService adminService, RegistrationService registrationService,
                              AuthService authService, EmployeeService employeeService, OrderService orderService) {
        this.deliverer = deliverer;
        this.delivererService = delivererService;
        this.adminService = adminService;
        this.registrationService = registrationService;
        this.authService = authService;
        this.employeeService = employeeService;
        this.orderService = orderService;

        initUI();
    }

    private void initUI() {
        setTitle("HaskOrders - Deliverer Dashboard - " + deliverer.getUsername());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(availableOrdersPanel);
        tabbedPane.addTab("Available Orders", scrollPane);

        tabbedPane.addTab("My Deliveries", myOrdersPanel);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTabs());

        JButton earningsBtn = new JButton("Show Earnings");
        earningsBtn.addActionListener(e -> showEarnings());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshBtn);
        topPanel.add(earningsBtn);
        topPanel.add(logoutBtn);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        refreshTabs();
        setVisible(true);
    }


    private void refreshTabs() {
        displayAvailableOrders();
        displayMyOrders();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginPage(delivererService, adminService, registrationService, authService, employeeService, orderService);
        }
    }

    private void displayAvailableOrders() {
        availableOrdersPanel.removeAll();
        availableOrdersPanel.setLayout(new BoxLayout(availableOrdersPanel, BoxLayout.Y_AXIS));
        List<Order> orders = delivererService.getAvailableDeliveries();

        for (Order order : orders) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("Order #" + order.getId() + " | Restaurant: " + order.getRestaurant().getName()));
            JButton acceptBtn = new JButton("Accept");

            acceptBtn.addActionListener(e -> {
                boolean accepted = delivererService.assignDeliverer(order.getId(), deliverer);
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
        List<Order> orders = delivererService.getAssignedDeliveries(deliverer);
        List<Order> completedOrders = delivererService.getCompletedDeliveries(deliverer);

        for (Order order : orders) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("Order #" + order.getId() + " | Restaurant: " + order.getRestaurant().getName()));

            JButton completeBtn = new JButton("Mark as Delivered");
            completeBtn.addActionListener(e -> {
                boolean done = delivererService.completeDelivery(order.getId());
                if (done) {
                    JOptionPane.showMessageDialog(this, "Delivery completed.");
                    delivererService.addOrderTotalToRevenue(deliverer, order);
                    refreshTabs();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to complete delivery.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(completeBtn);
            myOrdersPanel.add(panel);
        }

        if (!completedOrders.isEmpty()) {
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(Color.GREEN);
            headerPanel.add(new JLabel("Completed Orders"));
            myOrdersPanel.add(headerPanel);
        }

        for (Order order : completedOrders) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("Completed Order #" + order.getId() + " | Restaurant: " + order.getRestaurant().getName()));
            myOrdersPanel.add(panel);
        }

        myOrdersPanel.revalidate();
        myOrdersPanel.repaint();
    }


    private void showEarnings() {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        double threshold = 500.0;

        double earnings = delivererService.calculateDelivererRevenue(deliverer, start, end);
        boolean bonusUnlocked = delivererService.checkAndApplyBonus(deliverer, threshold);

        JOptionPane.showMessageDialog(this,
                "Earnings (total): " + String.format("%.2f", earnings) + " BGN" +
                        "\nBonus eligible: " + (bonusUnlocked ? "YES 🎉" : "NO"),
                "Earnings Summary",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

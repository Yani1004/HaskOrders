package com.example.haskorders.client;

import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(this::showChangePasswordDialog);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshBtn);
        topPanel.add(earningsBtn);
        topPanel.add(logoutBtn);
        topPanel.add(changePasswordButton);

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

    private void showChangePasswordDialog(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        JPasswordField currentPasswordField = new JPasswordField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        panel.add(new JLabel("Current Password:"));
        panel.add(currentPasswordField);
        panel.add(new JLabel("New Password (min 8 characters):"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!currentPassword.equals(deliverer.getPassword())) {
                showErrorMessage("Current password is incorrect");
                return;
            }

            if (newPassword.length() < 8) {
                showErrorMessage("Password must be at least 8 characters long");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showErrorMessage("New passwords do not match");
                return;
            }

            if (newPassword.equals(currentPassword)) {
                showErrorMessage("New password must be different from current password");
                return;
            }

            deliverer.setPassword(newPassword);

            authService.saveUser(deliverer);

            JOptionPane.showMessageDialog(
                    this,
                    "Password changed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

package bg.haskorders.delivery.deliverer;

import bg.haskorders.delivery.Main;
import bg.haskorders.delivery.authentication.LoginView;
import bg.haskorders.delivery.model.order.Order;
import bg.haskorders.delivery.model.user.User;
import bg.haskorders.delivery.constants.UIConstants;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

public class DelivererDashboard extends JFrame {

    private final User deliverer;
    private final DelivererService deliveryService;
    private final JTabbedPane tabbedPane;
    private final JPanel availableOrdersPanel;
    private final JPanel myOrdersPanel;

    public DelivererDashboard(User deliverer) {
        this.deliverer = deliverer;
        this.deliveryService = new DelivererService();

        setTitle("Deliverer Dashboard - " + deliverer.getUsername());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.BLACK);
        availableOrdersPanel = new JPanel();
        myOrdersPanel = new JPanel();
        availableOrdersPanel.setBackground(Color.WHITE);
        myOrdersPanel.setBackground(Color.WHITE);

        tabbedPane.addTab("Available Orders", availableOrdersPanel);
        tabbedPane.addTab("My Deliveries", myOrdersPanel);

        JButton refreshBtn = createStyledButton("Refresh", e -> refreshTabs());
        JButton earningsBtn = createStyledButton("Show Earnings", e -> showEarnings());
        JButton changePasswordBtn = createStyledButton("Change Password", e -> showChangePasswordDialog()); //CHANGED
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 40, 40)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginView(Main.userList);
            }
        });


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(UIConstants.PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topPanel.add(refreshBtn);
        topPanel.add(earningsBtn);
        topPanel.add(changePasswordBtn);
        topPanel.add(logoutBtn);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        refreshTabs();
        setVisible(true);
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(UIConstants.BACKGROUND_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.addActionListener(action);
        return button;
    }

    private void refreshTabs() {
        displayAvailableOrders();
        displayMyOrders();
    }

    private void displayAvailableOrders() {
        availableOrdersPanel.removeAll();
        availableOrdersPanel.setLayout(new BoxLayout(availableOrdersPanel, BoxLayout.Y_AXIS));
        availableOrdersPanel.setBackground(Color.WHITE);
        List<Order> orders = deliveryService.getAvailableOrders();

        for (Order order : orders) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.WHITE);
            panel.add(new JLabel("Order #" + order.getOrder_id() + " | Address: " + order.getDelivery_address()));
            JButton acceptBtn = createStyledButton("Accept", e -> {
                boolean accepted = deliveryService.takeOrder(order.getOrder_id(), deliverer.getUserId());
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
        myOrdersPanel.setBackground(Color.WHITE);

        List<Order> orders = deliveryService.getAssignedOrders(deliverer.getUserId());

        for (Order order : orders) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.white);
            panel.add(new JLabel("Order #" + order.getOrder_id() + " | Address: " + order.getDelivery_address()));

            JButton completeBtn = createStyledButton("Complete Delivery", e -> { //CHANGED
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
        double threshold = 500.0; // Не съм сигурен дали трябва да бъде начисляван само веднъж и дали ще се добави към заплатата на доставчика, също не знам как ще се образува тя. Може ли един доставчик да вземе над 1 бонус

        double earnings = deliveryService.getEarnings(deliverer.getUserId(), start, end);
        boolean hasBonus = deliveryService.hasBonus(deliverer.getUserId(), start, end, threshold);

        JOptionPane.showMessageDialog(this,
                "Earnings (last 30 days): $" + String.format("%.2f", earnings) +
                        "\nBonus eligible: " + (hasBonus ? "YES" : "NO"),
                "Earnings Summary",
                JOptionPane.INFORMATION_MESSAGE);
    }
    private void showChangePasswordDialog() {
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

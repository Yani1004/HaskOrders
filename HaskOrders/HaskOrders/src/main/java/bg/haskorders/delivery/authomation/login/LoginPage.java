package bg.haskorders.delivery.authomation.login;


import bg.haskorders.delivery.Main;
import bg.haskorders.delivery.admin.AdminPanel;
import bg.haskorders.delivery.authomation.register.RegisterPage;
import bg.haskorders.delivery.client.ClientDashboard;
import bg.haskorders.delivery.deliverer.DelivererDashboard;
import bg.haskorders.delivery.employee.EmployeeDashboard;
import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;
import bg.haskorders.delivery.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class LoginPage {
    private final JFrame frame;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AuthService authService;

    public LoginPage(List<User> userList) {
        this.authService = new AuthService(userList);

        frame = new JFrame("HaskOrders - Login");
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        setupFrame();
    }

    private void setupFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel mainPanel = createMainPanel();
        frame.add(mainPanel);
        frame.getRootPane().setDefaultButton(createLoginButton());

        frame.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);

        panel.add(createHeader(), BorderLayout.NORTH);
        panel.add(createFormPanel(), BorderLayout.CENTER);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createHeader() {
        JLabel header = new JLabel("HaskOrders Login", SwingConstants.CENTER);
        header.setFont(UIConstants.HEADER_FONT);
        header.setForeground(UIConstants.PRIMARY_COLOR);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        formPanel.add(createLabeledField("Username:", usernameField));
        formPanel.add(createLabeledField("Password:", passwordField));

        return formPanel;
    }

    private JPanel createLabeledField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.BLACK);
        panel.add(label);
        panel.add(field);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JButton loginButton = createLoginButton();
        JButton registerButton = createButton("Register", Color.DARK_GRAY, e -> openRegisterPage());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        return buttonPanel;
    }

    private JButton createLoginButton() {
        return createButton("Login", UIConstants.PRIMARY_COLOR, this::handleLogin);
    }

    private JButton createButton(String text, Color bgColor, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(action);
        return button;
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        User user = authService.authenticate(username, password);
        if (user != null) {
            frame.dispose();
            openDashboard(user);
        } else {
            showError("Invalid username or password");
            passwordField.setText("");
        }
    }

    private void openRegisterPage() {
        frame.dispose();
        new RegisterPage(new ArrayList<>());
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            switch (user.getRole()) {
                case ADMIN -> new AdminPanel((ArrayList<User>) Main.userList);
                case EMPLOYEE -> new EmployeeDashboard(user);
                case DELIVERER -> new DelivererDashboard(user, Main.orderRepository);
                case CLIENT -> new ClientDashboard(user, Main.restaurantRepository, Main.productRepository);
                default -> showError("Unknown role");
            }
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}


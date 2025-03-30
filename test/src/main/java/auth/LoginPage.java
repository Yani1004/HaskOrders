package auth;

import admin.AdminPanel;
import auth.RegisterPage;
import client.ClientDashboard;
import deliverer.DelivererDashboard;
import employee.EmployeeDashboard;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class LoginPage {
    private final ArrayList<User> userList;
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public static void main(String[] args) {
        // Initialize with default admin account
        ArrayList<User> userList = initializeUsers();
        SwingUtilities.invokeLater(() -> new LoginPage(userList));
    }

    static ArrayList<User> initializeUsers() {
        ArrayList<User> users = new ArrayList<>();

        // Create admin user with default credentials
        String adminPassword = "Admin@1234";
        users.add(new User(
                "admin",
                adminPassword,
                "System Administrator",
                "admin@haskorders.com",
                User.ROLE_ADMIN
        ));

        // Log admin credentials for debugging
        System.out.println("Admin account created:");
        System.out.println("Username: admin");
        System.out.println("Password: " + adminPassword);

        return users;
    }

    public LoginPage(ArrayList<User> userList) {
        this.userList = userList;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("HaskOrders - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);

        // Main panel with orange background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 190, 60)); // Your orange color

        // Header
        JLabel headerLabel = new JLabel("HaskOrders Login", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(70, 130, 180)); // Keep your blue text color
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Form Panel - Now with orange background
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(new Color(255, 190, 60)); // Orange background
        formPanel.setOpaque(true); // Ensure background is visible

        // Username Field - Transparent panel to show orange background
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        usernamePanel.setOpaque(false); // Transparent to show orange background
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.BLACK); // Better contrast
        usernameField = new JTextField(15);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        // Password Field - Transparent panel
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.setOpaque(false); // Transparent
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK); // Better contrast
        passwordField = new JPasswordField(15);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);

        // Buttons Panel - Semi-transparent
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 190, 60, 200)); // Orange with slight transparency
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton loginButton = createButton("Login", new Color(70, 130, 180));
        JButton registerButton = createButton("Register", new Color(100, 100, 100));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegisterPage(new ArrayList<>(userList));
        });

        // Enable Enter key for login
        frame.getRootPane().setDefaultButton(loginButton);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            User authenticatedUser = authenticate(username, password);
            if (authenticatedUser != null) {
                handleSuccessfulLogin(authenticatedUser);
            } else {
                showError("Invalid username or password");
                passwordField.setText("");
            }
        } catch (Exception ex) {
            showError("Authentication error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private User authenticate(String username, String password) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                if (BCrypt.checkpw(password, user.getPassword())) {
                    return user;
                }
            }
        }
        return null;
    }

    private void handleSuccessfulLogin(User user) {
        frame.dispose();
        SwingUtilities.invokeLater(() -> {
            switch (user.getRole()) {
                case User.ROLE_ADMIN:
                    new AdminPanel(new ArrayList<>(userList));
                    break;
                case User.ROLE_EMPLOYEE:
                    new EmployeeDashboard(user);
                    break;
                case User.ROLE_DELIVERER:
                    new DelivererDashboard(user);
                    break;
                default:
                    new ClientDashboard(user);
            }
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame,
                message,
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
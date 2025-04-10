package bg.haskorders.delivery.auth;

import bg.haskorders.delivery.admin.AdminPanel;
import bg.haskorders.delivery.client.ClientDashboard;
import bg.haskorders.delivery.deliverer.DelivererDashboard;
import bg.haskorders.delivery.employee.EmployeeDashboard;
import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class LoginPage {
    private final ArrayList<User> userList;
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

//    public static void main(String[] args) {
//        // Initialize with default admin account
//        ArrayList<User> userList = initializeUsers();
//        SwingUtilities.invokeLater(() -> new LoginPage(userList));
//    }

    public static ArrayList<User> initializeUsers() {
        ArrayList<User> users = new ArrayList<>();

        // Create admin user with plain-text password
        String adminPassword = "12345678";
        users.add(User.builder()
                .username("admin")
                .password(adminPassword) // Not hashed
                .name("System Administrator")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build());

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

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 190, 60));

        JLabel headerLabel = new JLabel("HaskOrders Login", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(70, 130, 180));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(new Color(255, 190, 60));

        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        usernamePanel.setOpaque(false);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.BLACK);
        usernameField = new JTextField(15);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        passwordField = new JPasswordField(15);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 190, 60, 200));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton loginButton = createButton("Login", new Color(70, 130, 180));
        JButton registerButton = createButton("Register", new Color(100, 100, 100));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegisterPage(new ArrayList<>(userList));
        });

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
                if (user.getPassword().equals(password)) { // Plain text comparison
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
                case ADMIN:
                    new AdminPanel(new ArrayList<>(userList));
                    break;
                case EMPLOYEE:
                    new EmployeeDashboard(user);
                    break;
                case DELIVERER:
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

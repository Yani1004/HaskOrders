package auth;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import static auth.LoginPage.initializeUsers;

public class RegisterPage {
    private JLabel errorWarning;
    private ArrayList<User> usersList;
    private JFrame frame;

    public RegisterPage(ArrayList<User> usersList) {
        this.usersList = usersList;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("HaskOrders - Register");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 190, 60));
        frame.setContentPane(panel);

        JLabel title = new JLabel("HaskOrders Register", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));

        // Form components
        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField(15);

        errorWarning = new JLabel("Password must be at least 8 characters");
        errorWarning.setForeground(Color.RED);
        errorWarning.setVisible(false);

        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPassText = new JPasswordField(15);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailText = new JTextField(15);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameText = new JTextField(15);

        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneText = new JTextField(15);

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressText = new JTextField(15);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        // Style buttons
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        backButton.setBackground(new Color(169, 169, 169));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        panel.add(userText, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        panel.add(passText, gbc);

        // Password warning
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 10, 8, 10);
        panel.add(errorWarning, gbc);

        // Confirm Password
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(confirmPassLabel, gbc);

        gbc.gridx = 1;
        panel.add(confirmPassText, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        panel.add(emailText, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        panel.add(nameText, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        panel.add(phoneText, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(addressLabel, gbc);

        gbc.gridx = 1;
        panel.add(addressText, gbc);

        // Register Button
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        // Back Button
        gbc.gridy = 10;
        panel.add(backButton, gbc);

        // Register button action
        registerButton.addActionListener((ActionEvent e) -> {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword()).trim();
            String confirmPassword = new String(confirmPassText.getPassword()).trim();
            String email = emailText.getText().trim();
            String name = nameText.getText().trim();
            String phone = phoneText.getText().trim();
            String address = addressText.getText().trim();

            // Validation checks
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() ||
                    name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                showError("All fields are required!");
                return;
            }

            if (password.length() < 8) {
                errorWarning.setVisible(true);
                showError("Password must be at least 8 characters!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match!");
                return;
            }

            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                showError("Please enter a valid email address!");
                return;
            }

            if (phone.length() < 10) {
                showError("Please enter a valid phone number (10 digits minimum)!");
                return;
            }

            if (isUsernameTaken(username)) {
                showError("Username already exists!");
                return;
            }

            // Create and add new user
            User newUser = new User(username, password, name, email, phone, address, User.ROLE_CLIENT);
            usersList.add(newUser);

            // Show success message
            JOptionPane.showMessageDialog(frame,
                    "Registration successful!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            clearForm(userText, passText, confirmPassText, emailText, nameText, phoneText, addressText);
        });

        // Back button action
        backButton.addActionListener(e -> {
            frame.dispose();
            new LoginPage(usersList);
        });

        // Password field listener for real-time validation
        passText.addActionListener(e -> {
            String password = new String(passText.getPassword());
            errorWarning.setVisible(password.length() > 0 && password.length() < 8);
        });

        frame.setVisible(true);
    }

    private boolean isUsernameTaken(String username) {
        for (User user : usersList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearForm(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
        errorWarning.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ArrayList<User> usersList = initializeUsers();
            new RegisterPage(usersList);
        });
    }
}
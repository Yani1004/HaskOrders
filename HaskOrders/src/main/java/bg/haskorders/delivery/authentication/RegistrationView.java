package bg.haskorders.delivery.authentication;

import bg.haskorders.delivery.authentication.service.CredentialsValidator;
import bg.haskorders.delivery.constants.UIConstants;
import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class RegistrationView {
    private JLabel errorWarning;
    private ArrayList<User> usersList;
    private JFrame frame;

    public RegistrationView(ArrayList<User> usersList) {
        this.usersList = usersList;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("HaskOrders - Register");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        frame.setContentPane(panel);

        JLabel title = new JLabel("HaskOrders Register", SwingConstants.CENTER);
        title.setFont(UIConstants.HEADER_FONT);

        // Input fields
        JTextField userText = new JTextField(15);
        JPasswordField passText = new JPasswordField(15);
        JPasswordField confirmPassText = new JPasswordField(15);
        JTextField emailText = new JTextField(15);
        JTextField nameText = new JTextField(15);
        JTextField phoneText = new JTextField(15);
        JTextField addressText = new JTextField(15);

        errorWarning = new JLabel("Password must be at least 8 characters");
        errorWarning.setForeground(Color.RED);
        errorWarning.setVisible(false);

        JButton registerButton = createButton("Register", UIConstants.PRIMARY_COLOR);
        JButton backToLoginButton = createButton("Back to login", UIConstants.PRIMARY_COLOR);

        // Add form components
        addFields(panel, title, userText, passText, confirmPassText,
                emailText, nameText, phoneText, addressText, registerButton, backToLoginButton);

        // Register button logic
        registerButton.addActionListener((ActionEvent e) -> {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword()).trim();
            String confirmPassword = new String(confirmPassText.getPassword()).trim();
            String email = emailText.getText().trim();
            String name = nameText.getText().trim();
            String phone = phoneText.getText().trim();
            String address = addressText.getText().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() ||
                    name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                showError("All fields are required!");
                return;
            }

            if (!CredentialsValidator.isPasswordValid(password)) {
                errorWarning.setVisible(true);
                showError("Password must be at least 8 characters!");
                return;
            }

            if (!CredentialsValidator.doPasswordsMatch(password, confirmPassword)) {
                showError("Passwords do not match!");
                return;
            }

            if (!CredentialsValidator.isEmailValid(email)) {
                showError("Please enter a valid email address!");
                return;
            }

            if (!CredentialsValidator.isPhoneValid(phone)) {
                showError("Please enter a valid phone number (10 digits)!");
                return;
            }

            if (!CredentialsValidator.isUsernameUnique(username, usersList)) {
                showError("Username already exists!");
                return;
            }

            User newUser = User.builder()
                    .username(username)
                    .password(password)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .role(Role.CLIENT)
                    .address(address)
                    .build();

            int newId = usersList.stream()
                    .map(User::getUserId)
                    .filter(id -> id != null)
                    .max(Integer::compareTo)
                    .orElse(1000) + 1;

            newUser.setUserId(newId);

            usersList.add(newUser);
            JOptionPane.showMessageDialog(frame, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(userText, passText, confirmPassText, emailText, nameText, phoneText, addressText);
            frame.dispose();
            new LoginView(usersList);
        });

        // Back to login button logic
        backToLoginButton.addActionListener((ActionEvent e) -> {
            frame.dispose();
            new LoginView(usersList);
        });

        // Password field live validation
        passText.addActionListener(e -> {
            String password = new String(passText.getPassword());
            errorWarning.setVisible(!password.isEmpty() && password.length() < 8);
        });

        frame.setVisible(true);
    }

    private void addFields(JPanel panel, JLabel title,
                           JTextField userText, JPasswordField passText, JPasswordField confirmPassText,
                           JTextField emailText, JTextField nameText, JTextField phoneText, JTextField addressText,
                           JButton registerButton, JButton backToLoginButton) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        String[] labels = {"Username:", "Password:", "Confirm Password:", "Email:", "Name:", "Phone:", "Address:"};
        JComponent[] fields = {
                userText, passText, confirmPassText,
                emailText, nameText, phoneText, addressText
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            panel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            panel.add(fields[i], gbc);

            if (labels[i].equals("Password:")) {
                gbc.gridy += 1;
                gbc.gridx = 1;
                panel.add(errorWarning, gbc);
            }
        }

        // Buttons
        gbc.gridy += 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        gbc.gridy += 1;
        panel.add(backToLoginButton, gbc);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
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
}

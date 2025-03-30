import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class RegisterPage {
    private JLabel errorWarning;
    private ArrayList<User> usersList;

    public RegisterPage(ArrayList<User> usersList) {
        this.usersList = usersList;
        initializeUIRP();
    }

    private void initializeUIRP() {
        // Main frame setup
        JFrame frame = new JFrame("Haskorders - Register");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 650);
        frame.setMinimumSize(new Dimension(500, 550));
        frame.setLocationRelativeTo(null);

        // Main panel with background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 190, 60));
        frame.setContentPane(panel);

        // Title label
        JLabel title = new JLabel("Haskorders Register", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.BLACK);

        // Form components
        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField(15);

        errorWarning = new JLabel("Password must be at least 8 characters");
        errorWarning.setForeground(Color.RED);
        errorWarning.setFont(new Font("Arial", Font.PLAIN, 10));
        errorWarning.setVisible(false);

        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPassText = new JPasswordField(15);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailText = new JTextField(15);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameText = new JTextField(25);

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneText = new JTextField(25);

        JLabel roleLabel = new JLabel("Account Type:");
        String[] roles = {"Client", "Employee", "Courier"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        // Style buttons
        registerButton.setBackground(new Color(219, 31, 58));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        backButton.setBackground(new Color(169, 169, 169));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        // GridBagConstraints setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

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

        // Name
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        panel.add(nameText, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        panel.add(emailText, gbc);
        //phone number --

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        panel.add(phoneText, gbc);
        // Role
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(roleLabel, gbc);

        gbc.gridx = 1;
        panel.add(roleCombo, gbc);

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
            String name = nameText.getText().trim();
            String email = emailText.getText().trim();
            String phone = phoneText.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            // Validation checks
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty()) {
                showError(frame, "All fields are required!");
                return;
            }

            if (password.length() < 8) {
                errorWarning.setVisible(true);
                showError(frame, "Password must be at least 8 characters!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showError(frame, "Passwords do not match!");
                return;
            }
//[A-z]*@aka.bg
            if (!email.contains("@") || !email.contains(".")) {
                showError(frame, "Please enter a valid email address!");
                return;
            }

            // Check if username already exists
            for (User user : usersList) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    showError(frame, "Username already exists!");
                    return;
                }
            }

            // Create and add new user
            User newUser = new User( username,  password,  name,  email,  phone,  role);
            usersList.add(newUser);

            // Print all users (for debugging)
            System.out.println("\nRegistered Users:");
            usersList.forEach(System.out::println);

            // Show success message
            JOptionPane.showMessageDialog(frame,
                    "Registration successful!\nUsername: " + username +
                            "\nRole: " + role,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            clearForm(userText, passText, confirmPassText, emailText, nameText);
        });

        // Back button action
        backButton.addActionListener((ActionEvent e) -> {
            frame.dispose();
            new LoginPage(usersList);
        });

        // Password field listener for real-time validation
        passText.addActionListener((ActionEvent e) -> {
            String password = new String(passText.getPassword());
            errorWarning.setVisible(password.length() > 0 && password.length() < 8);
        });

        frame.setVisible(true);
    }

    private void showError(JFrame frame, String message) {
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

            // Create shared users list
            ArrayList<User> usersList = new ArrayList<>();
            new RegisterPage(usersList);
        });
    }
}
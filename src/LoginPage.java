import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class LoginPage {
    private ArrayList<User> userList;

    public static void main(String[] args) {
        // Create the shared user list
        ArrayList<User> userList = new ArrayList<>();
        // Start with the login page
        SwingUtilities.invokeLater(() -> new LoginPage(userList));
    }

    public LoginPage(ArrayList<User> userList) {
        this.userList = userList;
        initializeUI();
    }

    private void initializeUI() {
        JFrame frame = new JFrame("Haskorders Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 190, 60));

        JLabel title = new JLabel("Haskorders Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Style buttons
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        registerButton.setBackground(new Color(169, 169, 169));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title label
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

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        // Register button
        gbc.gridy = 4;
        panel.add(registerButton, gbc);

        frame.setContentPane(panel);
        frame.setVisible(true);

        loginButton.addActionListener((ActionEvent e) -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login Successful");
                // Here you would typically open the main application window
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Invalid Credentials",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener((ActionEvent e) -> {
            frame.dispose();
            new RegisterPage(userList); // Pass the userList to RegisterPage
        });
    }

    private boolean authenticate(String username, String password) {
        // Check against the user list
        for (User user : userList) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
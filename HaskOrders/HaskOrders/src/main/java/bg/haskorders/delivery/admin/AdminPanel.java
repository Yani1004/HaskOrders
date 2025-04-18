package bg.haskorders.delivery.admin;

import bg.haskorders.delivery.authomation.login.LoginPage;
import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
public class AdminPanel {
    private ArrayList<User> usersList;
    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable userTable;
    private JPanel controlPanel;

    public AdminPanel(ArrayList<User> usersList) {
        this.usersList = usersList;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Admin Panel - User Management");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Create main components
        createUserTable();
        createControlPanel();

        // Layout
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(userTable), BorderLayout.CENTER);
        assert controlPanel != null;
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void createUserTable() {
        String[] columnNames = {"Username", "Name", "Email", "Current Role", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshUserTable();
    }

    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Role management components
        JLabel roleLabel = new JLabel("Change Role:");
        String[] roles = {String.valueOf(Role.CLIENT), String.valueOf(Role.EMPLOYEE), String.valueOf(Role.DELIVERER)};
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        JButton updateButton = new JButton("Update Role");
        updateButton.setBackground(new Color(70, 130, 180));
        updateButton.setForeground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(169, 169, 169));
        refreshButton.setForeground(Color.WHITE);

        // Add Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);

        // Add action listeners
        updateButton.addActionListener(e -> updateUserRole(roleCombo));
        refreshButton.addActionListener(e -> refreshUserTable());
        logoutButton.addActionListener(e -> logout());

        // Add components to control panel
        controlPanel.add(roleLabel);
        controlPanel.add(roleCombo);
        controlPanel.add(updateButton);
        controlPanel.add(refreshButton);
        controlPanel.add(logoutButton);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            frame.dispose();
            new LoginPage(usersList);
        }
    }

    private void refreshUserTable() {
        tableModel.setRowCount(0);
        for (User user : usersList) {
            tableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getRole().equals(Role.ADMIN) ? "Admin" : "Active"
            });
        }
    }

    private void updateUserRole(JComboBox<String> roleCombo) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 0);
            String newRole = (String) roleCombo.getSelectedItem();

            if (username.equals("admin")) {
                JOptionPane.showMessageDialog(frame,
                        "Cannot change role of admin account!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (User user : usersList) {
                if (user.getUsername().equals(username)) {
                    user.setRole(Role.valueOf(newRole));
                    tableModel.setValueAt(newRole, selectedRow, 3);
                    tableModel.setValueAt("Active", selectedRow, 4);
                    JOptionPane.showMessageDialog(frame,
                            "Role updated successfully!\n" +
                                    username + " is now a " + newRole,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please select a user first!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
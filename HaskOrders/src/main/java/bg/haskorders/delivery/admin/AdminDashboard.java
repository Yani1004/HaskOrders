package bg.haskorders.delivery.admin;

import bg.haskorders.delivery.authentication.LoginView;
import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard {
    private final UserService userManager;
    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable userTable;
    private JPanel controlPanel;

    public AdminDashboard(List<User> usersList) {
        this.userManager = new UserService(usersList);
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Admin Panel - User Management");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        createUserTable();
        createControlPanel();

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(userTable), BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void createUserTable() {
        String[] columnNames = {"Username", "Name", "Email", "Current Role"};
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

    private void refreshUserTable() {
        tableModel.setRowCount(0);
        for (User user : userManager.getUsers()) {
            tableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            });
        }
    }

    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel roleLabel = new JLabel("Change Role:");
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{
                Role.CLIENT.name(), Role.EMPLOYEE.name(), Role.DELIVERER.name()
        });

        JButton updateButton = UIUtils.createStyledButton("Update Role", new Color(70, 130, 180));
        JButton refreshButton = UIUtils.createStyledButton("Refresh", new Color(169, 169, 169));
        JButton logoutButton = UIUtils.createStyledButton("Logout", new Color(220, 53, 69));

        updateButton.addActionListener(e -> updateUserRole(roleCombo));
        refreshButton.addActionListener(e -> refreshUserTable());
        logoutButton.addActionListener(e -> logout());

        controlPanel.add(roleLabel);
        controlPanel.add(roleCombo);
        controlPanel.add(updateButton);
        controlPanel.add(refreshButton);
        controlPanel.add(logoutButton);
    }

    private void updateUserRole(JComboBox<String> roleCombo) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 0);
            Role selectedRole = Role.valueOf((String) roleCombo.getSelectedItem());

            if (userManager.updateUserRole(username, selectedRole)) {
                tableModel.setValueAt(selectedRole.name(), selectedRow, 3);
                JOptionPane.showMessageDialog(frame,
                        "Role updated successfully!\n" + username + " is now a " + selectedRole,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Cannot change role of admin account!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please select a user first!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            frame.dispose();
            new LoginView(userManager.getUsers());
        }
    }
}


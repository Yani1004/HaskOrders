package com.example.haskorders.client;

import com.example.haskorders.constants.UIConstants;
import com.example.haskorders.entities.*;
import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.order.Order_status;
import com.example.haskorders.entities.restaurant.CuisineType;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

public class EmployeeDashboard extends JFrame {
    private final User employee;
    private final DelivererService delivererService;
    private final AdminService adminService;
    private final RegistrationService registrationService;
    private final AuthService authService;
    private final EmployeeService employeeService;
    private final OrderService orderService;
    private DefaultTableModel restaurantTableModel;
    private DefaultTableModel productTableModel;

    public EmployeeDashboard(User employee, DelivererService delivererService, AdminService adminService,
                             RegistrationService registrationService, AuthService authService,
                             EmployeeService employeeService, OrderService orderService) {
        this.employee = employee;
        this.delivererService = delivererService;
        this.adminService = adminService;
        this.registrationService = registrationService;
        this.authService = authService;
        this.employeeService = employeeService;
        this.orderService = orderService;

        initializeUI();
    }

    private void initializeUI() {
        setTitle("HaskOrders - Employee Dashboard - " + employee.getUsername());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Restaurants", createRestaurantManagementPanel());
        tabbedPane.addTab("Products", createProductManagementPanel());
        tabbedPane.addTab("Orders", createOrderManagementPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel welcomeLabel = new JLabel("Welcome, " + employee.getUsername() + " (Employee)");
        welcomeLabel.setFont(UIConstants.HEADER_FONT);
        welcomeLabel.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage(delivererService, adminService, registrationService, authService, employeeService, orderService);
        });
        buttonPanel.add(changePasswordButton);

        buttonPanel.add(logoutButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);




        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createRestaurantManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = createStyledButton("Add Restaurant", this::addRestaurant);
        JButton refreshButton = createStyledButton("Refresh", e -> refreshRestaurantTable());
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        String[] columnNames = {"ID", "Name", "Address", "Cuisine", "Phone"};
        restaurantTableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(restaurantTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        refreshRestaurantTable();

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem deleteItem = new JMenuItem("Delete");
        editItem.addActionListener(e -> editSelectedRestaurant(table));
        deleteItem.addActionListener(e -> deleteSelectedRestaurant(table, restaurantTableModel));
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        table.setComponentPopupMenu(popupMenu);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProductManagementPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Restaurant> restaurantCombo = new JComboBox<>();
        refreshRestaurantComboBox(restaurantCombo);

        restaurantCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Restaurant r) {
                    value = r.getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        JButton filterButton = createStyledButton("Filter", e -> filterProducts(restaurantCombo));
        filterPanel.add(new JLabel("Filter by Restaurant:"));
        filterPanel.add(restaurantCombo);
        filterPanel.add(filterButton);
        mainPanel.add(filterPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Price", "Category", "Restaurant"};
        productTableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        refreshProductTable(productTableModel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = createStyledButton("Add Product", this::addProduct);
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createOrderManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Order ID", "Customer", "Total", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);

        JComboBox<Order_status> statusCombo = new JComboBox<>(Order_status.values());
        JButton filterButton = createStyledButton("Filter", e -> filterOrders(statusCombo, model));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Status:"));
        filterPanel.add(statusCombo);
        filterPanel.add(filterButton);

        JButton updateButton = createStyledButton("Update Status", e -> updateOrderStatus(table));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(updateButton);

        refreshOrderTable(model);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel reportPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField startDateField = new JTextField(LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
        JTextField endDateField = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        JTextField deliveryPersonField = new JTextField();
        reportPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        reportPanel.add(startDateField);
        reportPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        reportPanel.add(endDateField);
        reportPanel.add(new JLabel("Delivery Person ID:"));
        reportPanel.add(deliveryPersonField);
        panel.add(reportPanel, BorderLayout.CENTER);

        JButton revenueButton = createStyledButton("Company Revenue", e -> showRevenueReport(startDateField, endDateField));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(revenueButton);
        JButton deliveryEarningsButton = new JButton("Delivery Earnings");
        deliveryEarningsButton.addActionListener(e -> {
            showDeliveryEarningsReport(deliveryPersonField, startDateField, endDateField);
        });
        buttonPanel.add(deliveryEarningsButton);

        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);

        panel.add(reportPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshRestaurantTable() {
        if (restaurantTableModel == null) return;
        restaurantTableModel.setRowCount(0); // clear table
        List<Restaurant> restaurants = employeeService.getAllRestaurants();
        for (Restaurant r : restaurants) {
            restaurantTableModel.addRow(new Object[]{
                    r.getId(),
                    r.getName(),
                    r.getAddress(),
                    r.getCuisineType(),
                    r.getPhone()
            });
        }
    }

    private void addRestaurant(ActionEvent e) {
        JDialog dialog = new JDialog(this, "Add New Restaurant", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<CuisineType> cuisineCombo = new JComboBox<>(CuisineType.values());

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Cuisine Type:"));
        dialog.add(cuisineCombo);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(ev -> {
            Restaurant newRestaurant = Restaurant.builder()
                    .name(nameField.getText())
                    .address(addressField.getText())
                    .phone(phoneField.getText())
                    .email(emailField.getText())
                    .cuisineType((CuisineType)cuisineCombo.getSelectedItem())
                    .imagePath("/images/restaurants/default.jpg")
                    .build();

            employeeService.addRestaurant(newRestaurant);
            refreshRestaurantTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Restaurant added successfully!");
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.setVisible(true);
    }

    private void editSelectedRestaurant(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant to edit.");
            return;
        }

        int restaurantId = (int) table.getValueAt(selectedRow, 0);
        Restaurant restaurant = employeeService.getRestaurantById(restaurantId);
        if (restaurant == null) return;

        JDialog dialog = new JDialog(this, "Edit Restaurant", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField nameField = new JTextField(restaurant.getName());
        JTextField addressField = new JTextField(restaurant.getAddress());
        JTextField phoneField = new JTextField(restaurant.getPhone());
        JTextField emailField = new JTextField(restaurant.getEmail());
        JComboBox<CuisineType> cuisineCombo = new JComboBox<>(CuisineType.values());
        cuisineCombo.setSelectedItem(restaurant.getCuisineType());

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Cuisine Type:"));
        dialog.add(cuisineCombo);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            restaurant.setName(nameField.getText());
            restaurant.setAddress(addressField.getText());
            restaurant.setPhone(phoneField.getText());
            restaurant.setEmail(emailField.getText());
            restaurant.setCuisineType((CuisineType)cuisineCombo.getSelectedItem());

            employeeService.updateRestaurant(restaurant); // make sure this method exists
            dialog.dispose();
            refreshRestaurantTable();
            JOptionPane.showMessageDialog(this, "Restaurant updated successfully!");
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.setVisible(true);
    }

    private void deleteSelectedRestaurant(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant to delete.");
            return;
        }

        int restaurantId = (int) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this restaurant?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            employeeService.removeRestaurant(restaurantId); // make sure this method exists
            refreshRestaurantTable();
            JOptionPane.showMessageDialog(this, "Restaurant deleted.");
        }
    }

    private void refreshProductTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Product> products = employeeService.getAllProducts();
        for (Product p : products) {
            Restaurant r = employeeService.getRestaurantById(p.getRestaurant().getId());
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getCategory(),
                    r != null ? r.getName() + " (ID: " + r.getId() + ")" : "N/A"
            });
        }
    }

    private void refreshRestaurantComboBox(JComboBox<Restaurant> comboBox) {
        comboBox.removeAllItems();
        List<Restaurant> restaurants = employeeService.getAllRestaurants();
        restaurants.forEach(comboBox::addItem);
    }

    private void filterProducts(JComboBox<Restaurant> comboBox) {
        Restaurant selectedRestaurant = (Restaurant) comboBox.getSelectedItem();
        if (selectedRestaurant == null) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Product> products = employeeService.getProductsByRestaurant(selectedRestaurant.getId());

        productTableModel.setRowCount(0);

        for (Product p : products) {
            Restaurant r = employeeService.getRestaurantById(p.getRestaurant().getId());
            productTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getCategory(),
                    r != null ? r.getName() + " (ID: " + r.getId() + ")" : "N/A"
            });
        }
    }

    private void addProduct(ActionEvent e) {
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField categoryField = new JTextField();

        JComboBox<Restaurant> restaurantCombo = new JComboBox<>();
        employeeService.getAllRestaurants().forEach(restaurantCombo::addItem);

        restaurantCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Restaurant restaurant) {
                    value = restaurant.getName() + " (ID: " + restaurant.getId() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        dialog.add(new JLabel("Name:")); dialog.add(nameField);
        dialog.add(new JLabel("Description:")); dialog.add(descField);
        dialog.add(new JLabel("Price:")); dialog.add(priceField);
        dialog.add(new JLabel("Category:")); dialog.add(categoryField);
        dialog.add(new JLabel("Restaurant:")); dialog.add(restaurantCombo);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(ev -> {
            try {
                double price = Double.parseDouble(priceField.getText());
                Restaurant selectedRestaurant = (Restaurant) restaurantCombo.getSelectedItem();

                Product product = Product.builder()
                        .name(nameField.getText())
                        .description(descField.getText())
                        .price(price)
                        .category(categoryField.getText())
                        .restaurant(selectedRestaurant)
                        .build();
                employeeService.addProduct(product);
                dialog.dispose();
                refreshProductTable(productTableModel);
                JOptionPane.showMessageDialog(this, "Product added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(new JLabel());
        dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    private void refreshOrderTable(DefaultTableModel model) {
        model.setRowCount(0);

        List<Order> orders = employeeService.getAllOrders();

        for (Order o : orders) {
            User customer = o.getCustomer();
            String customerName = resolveCustomerName(o);

            double totalAmount = orderService.calculateOrderTotal(o);

            model.addRow(new Object[]{
                    o.getId(),
                    customerName,
                    String.format("%.2f", totalAmount),
                    o.getStatus(),
                    o.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            });
        }
    }

    private String resolveCustomerName(Order order) {
        return order.getCustomer() != null
                ? order.getCustomer().getUsername() + " (ID: " + order.getCustomer().getId() + ")"
                : "Unknown";
    }

    private void filterOrders(JComboBox<Order_status> statusCombo, DefaultTableModel model) {
        Order_status selectedStatus = (Order_status) statusCombo.getSelectedItem();
        model.setRowCount(0);

        List<Order> orders = employeeService.getAllOrders();
        for (Order order : orders) {
            if (selectedStatus == null || order.getStatus() == selectedStatus) {
                String customerName = resolveCustomerName(order);

                double totalAmount = orderService.calculateOrderTotal(order);

                model.addRow(new Object[]{
                        order.getId(),
                        customerName,
                        String.format("%.2f", totalAmount),
                        order.getStatus(),
                        order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                });
            }
        }
    }

    private void updateOrderStatus(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int orderId = (int) model.getValueAt(selectedRow, 0);
        Order_status currentStatus = (Order_status) model.getValueAt(selectedRow, 3);

        Order_status newStatus = (Order_status) JOptionPane.showInputDialog(
                this,
                "Select new status for order #" + orderId,
                "Update Order Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Order_status.values(),
                currentStatus);

        if (newStatus != null && newStatus != currentStatus) {
            boolean success = orderService.updateOrderStatus(orderId, newStatus);
            if (success) {
                model.setValueAt(newStatus, selectedRow, 3);
                JOptionPane.showMessageDialog(this, "Order status updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order status", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRevenueReport(JTextField startDateField, JTextField endDateField) {
        try {
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both start and end dates.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDateTime startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
            LocalDateTime endDate = LocalDateTime.parse(endDateStr + "T23:59:59");

            double revenue = orderService.calculateCompanyRevenue(startDate, endDate);

            JOptionPane.showMessageDialog(this,
                    String.format("Total revenue from %s to %s:\nBGN%.2f",
                            startDateStr, endDateStr, revenue),
                    "Company Revenue",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDeliveryEarningsReport(JTextField idField, JTextField startField, JTextField endField) {
        try {
            int deliveryPersonId = Integer.parseInt(idField.getText().trim());
            LocalDateTime start = LocalDateTime.parse(startField.getText().trim() + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endField.getText().trim() + "T23:59:59");
            User deliverer = employeeService.getUserById(deliveryPersonId);

            double earnings = delivererService.calculateDelivererRevenue(deliverer, start, end);

            JOptionPane.showMessageDialog(this,
                    String.format("Earnings for Delivery Person ID %d from %s to %s:\n%.2f лв",
                            deliveryPersonId,
                            startField.getText().trim(),
                            endField.getText().trim(),
                            earnings),
                    "Delivery Earnings Report",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.addActionListener(action);
        return button;
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

            if (!currentPassword.equals(employee.getPassword())) {
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

            employee.setPassword(newPassword);

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

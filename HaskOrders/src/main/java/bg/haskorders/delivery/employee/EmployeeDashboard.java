package bg.haskorders.delivery.employee;

import bg.haskorders.delivery.Main;
import bg.haskorders.delivery.authentication.LoginView;
import bg.haskorders.delivery.constants.UIConstants;
import bg.haskorders.delivery.model.*;
import bg.haskorders.delivery.model.order.Order;
import bg.haskorders.delivery.model.order.OrderStatus;
import bg.haskorders.delivery.model.restaurant.Restaurant;
import bg.haskorders.delivery.model.user.User;
import bg.haskorders.delivery.model.restaurant.CuisineType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class EmployeeDashboard {
    private final JFrame frame;
    private final User employee;
    private DefaultTableModel restaurantTableModel;
    private DefaultTableModel productTableModel;
    private final EmployeeService employeeService = new EmployeeService();

    public EmployeeDashboard(User employee) {
        this.employee = employee;

        frame = new JFrame("HaskOrders - Employee Dashboard");
        initializeUI();
    }

    private void initializeUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabbed Pane for different functionalities
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Restaurants", createRestaurantManagementPanel());
        tabbedPane.addTab("Products", createProductManagementPanel());
        tabbedPane.addTab("Orders", createOrderManagementPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel welcomeLabel = new JLabel("Welcome, " + employee.getUsername() + " (Employee)");
        welcomeLabel.setFont(UIConstants.HEADER_FONT);
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginView(Main.userList);
        });

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createRestaurantManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = createStyledButton("Add Restaurant", this::addRestaurant);
        JButton refreshButton = createStyledButton("Refresh", e -> refreshRestaurantTable());
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        // Restaurant table
        String[] columnNames = {"ID", "Name", "Address", "Cuisine", "Phone"};
        restaurantTableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(restaurantTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Populate table
        refreshRestaurantTable();

        // Right-click menu
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

        // Filter Panel (top)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Restaurant> restaurantCombo = new JComboBox<>();
        refreshRestaurantComboBox(restaurantCombo);

        // Show only restaurant names in combo
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

        // Product Table (center)
        String[] columnNames = {"ID", "Name", "Price", "Category", "Restaurant"};
        productTableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        refreshProductTable(productTableModel);

        // Button Panel (bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = createStyledButton("Add Product", this::addProduct);
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createOrderManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Order table
        String[] columnNames = {"Order ID", "Customer", "Total", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);

        // Status filter
        JComboBox<OrderStatus> statusCombo = new JComboBox<>(OrderStatus.values());
        JButton filterButton = createStyledButton("Filter", e -> filterOrders(statusCombo, model));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Status:"));
        filterPanel.add(statusCombo);
        filterPanel.add(filterButton);

        // Action buttons
        JButton updateButton = createStyledButton("Update Status", e -> updateOrderStatus(table));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(updateButton);

        // Initial load
        refreshOrderTable(model);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Date selection
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

        // Report buttons
        JButton revenueButton = createStyledButton("Company Revenue", e -> showRevenueReport(startDateField, endDateField));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(revenueButton);
        JButton deliveryEarningsButton = new JButton("Delivery Earnings");
        deliveryEarningsButton.addActionListener(e -> {
            showDeliveryEarningsReport(deliveryPersonField, startDateField, endDateField);
        });
        buttonPanel.add(deliveryEarningsButton);

        // Results area
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
                    r.getRestaurant_id(),
                    r.getName(),
                    r.getAddress(),
                    r.getCuisineType(),
                    r.getPhone()
            });
        }
    }

    private void addRestaurant(ActionEvent e) {
        JDialog dialog = new JDialog(frame, "Add New Restaurant", true);
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
                    .restaurant_id(generateNewRestaurantId())
                    .name(nameField.getText())
                    .address(addressField.getText())
                    .phone(phoneField.getText())
                    .email(emailField.getText())
                    .cuisineType((CuisineType)cuisineCombo.getSelectedItem())
                    .imagePath("/images/restaurants/default.jpg")
                    .build();

            employeeService.addRestaurant(newRestaurant);
            refreshRestaurantTable(); // Use the parameterless version
            dialog.dispose();
            JOptionPane.showMessageDialog(frame, "Restaurant added successfully!");
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.setVisible(true);
    }

    private int generateNewRestaurantId() {
        return employeeService.getAllRestaurants().stream()
                .mapToInt(Restaurant::getRestaurant_id)
                .max()
                .orElse(0) + 1;
    }

    private void editSelectedRestaurant(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a restaurant to edit.");
            return;
        }

        int restaurantId = (int) table.getValueAt(selectedRow, 0);
        Restaurant restaurant = employeeService.getRestaurantById(restaurantId);
        if (restaurant == null) return;

        JDialog dialog = new JDialog(frame, "Edit Restaurant", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 250);

        // Pre-fill form fields
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
            JOptionPane.showMessageDialog(frame, "Restaurant updated successfully!");
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.setVisible(true);
    }

    private void deleteSelectedRestaurant(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a restaurant to delete.");
            return;
        }

        int restaurantId = (int) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this restaurant?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            employeeService.removeRestaurant(restaurantId); // make sure this method exists
            refreshRestaurantTable();
            JOptionPane.showMessageDialog(frame, "Restaurant deleted.");
        }
    }

    // Helper methods for product operations
    private void refreshProductTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Product> products = employeeService.getAllProducts();
        for (Product p : products) {
            Restaurant r = employeeService.getRestaurantById(p.getRestaurantID());
            model.addRow(new Object[]{
                    p.getProduct_id(),
                    p.getName(),
                    p.getPrice(),
                    p.getCategory(),
                    r != null ? r.getName() + " (ID: " + r.getRestaurant_id() + ")" : "N/A"
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
            JOptionPane.showMessageDialog(frame, "Please select a restaurant.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Product> products = employeeService.getProductsForRestaurant(selectedRestaurant.getRestaurant_id());

        productTableModel.setRowCount(0);

        for (Product p : products) {
            Restaurant r = employeeService.getRestaurantById(p.getRestaurantID());
            productTableModel.addRow(new Object[]{
                    p.getProduct_id(),
                    p.getName(),
                    p.getPrice(),
                    p.getCategory(),
                    r != null ? r.getName() + " (ID: " + r.getRestaurant_id() + ")" : "N/A"
            });
        }
    }

    private void addProduct(ActionEvent e) {
        JDialog dialog = new JDialog(frame, "Add Product", true);
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
                    value = restaurant.getName() + " (ID: " + restaurant.getRestaurant_id() + ")";
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
                        .product_id(generateNewProductId())
                        .name(nameField.getText())
                        .description(descField.getText())
                        .price(price)
                        .category(categoryField.getText())
                        .restaurantID(selectedRestaurant.getRestaurant_id())
                        .build();
                employeeService.addProduct(product);
                dialog.dispose();
                refreshProductTable(productTableModel);
                JOptionPane.showMessageDialog(frame, "Product added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(new JLabel()); // filler
        dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    private int generateNewProductId() {
        return employeeService.getAllProducts().stream()
                .mapToInt(Product::getProduct_id)
                .max()
                .orElse(0) + 1;
    }

    // Helper methods for order operations
    private void refreshOrderTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing rows

        List<Order> orders = employeeService.getAllOrders();

        for (Order o : orders) {
            // Find the user by user_id (you need access to userList)
            String customerName = resolveCustomerName(o.getUser_id());

            model.addRow(new Object[]{
                    o.getOrder_id(),
                    customerName,
                    String.format("%.2f", o.getTotal_amount()),
                    o.getStatus(),
                    o.getOrder_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            });
         }
    }

    private String resolveCustomerName(int userId) {
        for (User user : Main.userList) {
            if (Objects.equals(user.getUserId(), userId)) {
                return user.getUsername() + " (ID: " + user.getUserId() + ")";
            }
        }
        return "Unknown";
    }

    private void filterOrders(JComboBox<OrderStatus> statusCombo, DefaultTableModel model) {
        OrderStatus selectedStatus = (OrderStatus) statusCombo.getSelectedItem();
        model.setRowCount(0);

        List<Order> orders = employeeService.getAllOrders();
        for (Order order : orders) {
            if (selectedStatus == null || order.getStatus() == selectedStatus) {
                String customerName = "Unknown";
                for (User user : Main.userList) {
                    if (Objects.equals(user.getUserId(), order.getUser_id())) {
                        customerName = user.getUsername() + " (ID: " + user.getUserId() + ")";
                        break;
                    }
                }

                customerName = resolveCustomerName(order.getUser_id());
                model.addRow(new Object[]{
                        order.getOrder_id(),
                        customerName,
                        String.format("%.2f", order.getTotal_amount()),
                        order.getStatus(),
                        order.getOrder_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                });
            }
        }
    }

    private void updateOrderStatus(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select an order to update", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int orderId = (int) model.getValueAt(selectedRow, 0);
        OrderStatus currentStatus = (OrderStatus) model.getValueAt(selectedRow, 3);

        // Show dialog to select new status
        OrderStatus newStatus = (OrderStatus) JOptionPane.showInputDialog(
                frame,
                "Select new status for order #" + orderId,
                "Update Order Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                OrderStatus.values(),
                currentStatus);

        if (newStatus != null && newStatus != currentStatus) {
            boolean success = employeeService.updateStatus(orderId, newStatus);
            if (success) {
                model.setValueAt(newStatus, selectedRow, 3);
                JOptionPane.showMessageDialog(frame, "Order status updated successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update order status", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper methods for reports
    private void showRevenueReport(JTextField startDateField, JTextField endDateField) {
        try {
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both start and end dates.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDateTime startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
            LocalDateTime endDate = LocalDateTime.parse(endDateStr + "T23:59:59");

            double revenue = employeeService.calculateCompanyRevenue(startDate, endDate);

            JOptionPane.showMessageDialog(frame,
                    String.format("Total revenue from %s to %s:\n$%.2f",
                            startDateStr, endDateStr, revenue),
                    "Company Revenue",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDeliveryEarningsReport(JTextField idField, JTextField startField, JTextField endField) {
        try {
            int deliveryPersonId = Integer.parseInt(idField.getText().trim());
            LocalDateTime start = LocalDateTime.parse(startField.getText().trim() + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endField.getText().trim() + "T23:59:59");

            double earnings = employeeService.calculateDeliveryEarnings(deliveryPersonId, start, end);

            JOptionPane.showMessageDialog(frame,
                    String.format("Earnings for Delivery Person ID %d from %s to %s:\n%.2f лв",
                            deliveryPersonId,
                            startField.getText().trim(),
                            endField.getText().trim(),
                            earnings),
                    "Delivery Earnings Report",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }
}

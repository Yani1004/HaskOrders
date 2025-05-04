package com.example.haskorders.client.client;

import com.example.haskorders.client.LoginPage;
import com.example.haskorders.client.cart.Cart;
import com.example.haskorders.client.cart.MemoryCartStorage;
import com.example.haskorders.entities.restaurant.CuisineType;
import com.example.haskorders.entities.Product;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ClientDashboard extends JFrame {
    private final User client;
    private final DelivererService delivererService;
    private final AdminService adminService;
    private final RegistrationService registrationService;
    private final AuthService authService;
    private final EmployeeService employeeService;
    private final OrderService orderService;
    private Cart sharedCart;

    private JPanel filterPanel;
    private JPanel restaurantsPanel;
    private boolean filtersShown = false;

    private final ButtonGroup cuisineGroup = new ButtonGroup();
    //private final ButtonGroup ratingGroup = new ButtonGroup();

    public ClientDashboard(User client, DelivererService delivererService, AdminService adminService,
                             RegistrationService registrationService, AuthService authService,
                             EmployeeService employeeService, OrderService orderService) {
        this.client = client;
        this.sharedCart = new Cart(client, new MemoryCartStorage());
        this.delivererService = delivererService;
        this.adminService = adminService;
        this.registrationService = registrationService;
        this.authService = authService;
        this.employeeService = employeeService;
        this.orderService = orderService;

        initializeUI();
    }

    private void initializeUI() {
        setTitle("HaskOrders - Client Dashboard - " + client.getUsername());
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        filterPanel = createFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.WEST);


        JButton clearFiltersButton = getJButton();

        clearFiltersButton.addActionListener(e -> clearFilters());

        JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonHolder.setBackground(filterPanel.getBackground());
        buttonHolder.add(clearFiltersButton);


        filterPanel.add(buttonHolder);

        restaurantsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        restaurantsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        restaurantsPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(restaurantsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        loadRestaurants();
        setVisible(true);
    }

    private void clearFilters() {
        cuisineGroup.clearSelection();
        //ratingGroup.clearSelection();
        loadRestaurants();
    }
    private JButton getJButton() {
        JButton clearFiltersButton = new JButton("Clear Filters");
        clearFiltersButton.setFont(new Font("Arial", Font.PLAIN, 12));
        clearFiltersButton.setPreferredSize(new Dimension(100, 30));
        clearFiltersButton.setFocusPainted(false);
        clearFiltersButton.setBackground(new Color(200, 200, 200));
        clearFiltersButton.setForeground(Color.BLACK);
        clearFiltersButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        clearFiltersButton.addActionListener(e -> clearFilters());
        return clearFiltersButton;
    }


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 87, 34));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel titleLabel = new JLabel("Welcome to HaskOrders!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);



        JButton filterButton = new JButton("Filter");
        styleButton(filterButton, new Color(255, 112, 67));
        filterButton.addActionListener(this::toggleFilters);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        filterButton = new JButton("Filter");
        styleButton(filterButton, new Color(255, 112, 67));
        filterButton.addActionListener(this::toggleFilters);
        buttonPanel.add(filterButton);

        JButton changePasswordButton = new JButton("Change Password");
        styleButton(changePasswordButton, new Color(255, 193, 7));
        changePasswordButton.addActionListener(this::showChangePasswordDialog);
        buttonPanel.add(changePasswordButton);

        JButton trackOrderButton = new JButton("Track My Order");
        styleButton(trackOrderButton, new Color(33, 150, 243)); // blue
        trackOrderButton.addActionListener(e -> new OrderStatusPage(client, orderService));
        buttonPanel.add(trackOrderButton);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(244, 67, 54));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginPage(delivererService, adminService, registrationService, authService, employeeService, orderService);
        });
        buttonPanel.add(logoutButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        return headerPanel;
    }
    private void showChangePasswordDialog(ActionEvent e) {
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

            if (!currentPassword.equals(client.getPassword())) {
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

            client.setPassword(newPassword);

            authService.saveUser(client);

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

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(255, 248, 225));
        panel.setVisible(false);

        panel.add(createCuisineFilterSection());
        //panel.add(createRatingFilterSection());

        return panel;
    }

    private JPanel createCuisineFilterSection() {
        JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
        sectionPanel.setBorder(BorderFactory.createTitledBorder("Cuisine"));
        sectionPanel.setBackground(new Color(255, 248, 225));

        for (CuisineType cuisine : CuisineType.values()) {
            JRadioButton radioButton = new JRadioButton(cuisine.toString());
            radioButton.setActionCommand(cuisine.name());
            radioButton.setBackground(new Color(255, 248, 225));
            cuisineGroup.add(radioButton);
            sectionPanel.add(radioButton);
            radioButton.addActionListener(e -> loadRestaurants());
        }

        return sectionPanel;
    }
    /*

    private JPanel createRatingFilterSection() {
        JPanel sectionPanel = new JPanel(new GridLayout(0, 1));
        sectionPanel.setBorder(BorderFactory.createTitledBorder("Rating"));
        sectionPanel.setBackground(new Color(255, 248, 225));

        String[] options = {"4+ Stars", "3+ Stars", "Any"};
        for (String option : options) {
            JRadioButton radioButton = new JRadioButton(option);
            radioButton.setActionCommand(option);
            radioButton.setBackground(new Color(255, 248, 225));
            ratingGroup.add(radioButton);
            sectionPanel.add(radioButton);
            radioButton.addActionListener(e -> loadRestaurants());
        }

        return sectionPanel;
    }
        */
    private void toggleFilters(ActionEvent e) {
        filtersShown = !filtersShown;
        filterPanel.setVisible(filtersShown);
        this.revalidate();
    }

    private void loadRestaurants() {
        restaurantsPanel.removeAll();

        List<Restaurant> restaurants = employeeService.getAllRestaurants();

        ButtonModel selectedCuisine = cuisineGroup.getSelection();
        if (selectedCuisine != null) {
            try {
                CuisineType selectedType = CuisineType.valueOf(selectedCuisine.getActionCommand());
                restaurants = restaurants.stream()
                        .filter(r -> r.getCuisineType() == selectedType)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {
            }
        }

        /*
        ButtonModel selectedRating = ratingGroup.getSelection();
        if (selectedRating != null) {
            String ratingText = selectedRating.getActionCommand();
            if (ratingText.contains("4")) {
                restaurants = restaurants.stream()
                        .filter(r -> r.getRating() >= 4.0)
                        .collect(Collectors.toList());
            } else if (ratingText.contains("3")) {
                restaurants = restaurants.stream()
                        .filter(r -> r.getRating() >= 3.0)
                        .collect(Collectors.toList());
            }
        }
        */
        for (Restaurant restaurant : restaurants) {
            addRestaurantCard(restaurantsPanel, restaurant);
        }

        restaurantsPanel.revalidate();
        restaurantsPanel.repaint();
    }

    private void addRestaurantCard(JPanel panel, Restaurant restaurant) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        ImageIcon restaurantIcon = loadRestaurantImage(restaurant.getImagePath());
        JLabel imageLabel = new JLabel(restaurantIcon);
        imageLabel.setPreferredSize(new Dimension(150, 100));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(238, 238, 238)));

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(restaurant.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel cuisineLabel = new JLabel(restaurant.getCuisineType().toString());
        cuisineLabel.setForeground(new Color(117, 117, 117));

        //JLabel ratingLabel = new JLabel(String.format("%.1f ★", restaurant.getRating()));
        //  ratingLabel.setForeground(new Color(255, 152, 0));

        infoPanel.add(nameLabel);
        infoPanel.add(cuisineLabel);
        //   infoPanel.add(ratingLabel);

        JButton viewMenuButton = new JButton("View Menu");
        styleButton(viewMenuButton, new Color(76, 175, 80));
        viewMenuButton.addActionListener(e -> {
            List<Product> products = employeeService.getProductsByRestaurant(restaurant.getId());
            this.setVisible(false);
            new RestaurantMenuPage(restaurant, products, sharedCart, client, delivererService, adminService,
                    registrationService, authService, employeeService, orderService);
        });

        card.add(imageLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(viewMenuButton, BorderLayout.SOUTH);

        panel.add(card);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }
    private ImageIcon loadRestaurantImage(String path) {
        if (path == null || path.isEmpty()) {
            return createDefaultRestaurantIcon();
        }

        try {
            java.net.URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                return scaleImageIcon(icon, 150, 100);
            }

            if (new File(path).exists()) {
                ImageIcon icon = new ImageIcon(path);
                return scaleImageIcon(icon, 150, 100);
            }

            return createDefaultRestaurantIcon();
        } catch (Exception e) {
            System.err.println("Error loading restaurant image: " + path);
            return createDefaultRestaurantIcon();
        }
    }

    private ImageIcon scaleImageIcon(ImageIcon icon, int width, int height) {
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private ImageIcon createDefaultRestaurantIcon() {
        BufferedImage image = new BufferedImage(150, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, 150, 100);

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String text = "No Image";
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, 75 - textWidth/2, 50);

        g.dispose();
        return new ImageIcon(image);
    }
}

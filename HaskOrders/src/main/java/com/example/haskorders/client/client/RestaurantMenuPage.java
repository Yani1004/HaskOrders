package com.example.haskorders.client.client;

import com.example.haskorders.client.cart.Cart;
import com.example.haskorders.entities.Product;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;

public class RestaurantMenuPage extends JFrame {
    private final Restaurant restaurant;
    private final User user;
    private final DelivererService delivererService;
    private final AdminService adminService;
    private final RegistrationService registrationService;
    private final AuthService authService;
    private final EmployeeService employeeService;
    private final OrderService orderService;
    private final List<Product> productList;
    private final Cart cart;
    private JButton viewCartButton;
    private final Runnable updateCartCallback;

    public RestaurantMenuPage(Restaurant restaurant, List<Product> productList, Cart cart, User user, DelivererService delivererService,
                              AdminService adminService, RegistrationService registrationService, AuthService authService,
                              EmployeeService employeeService, OrderService orderService) {
        this.restaurant = restaurant;
        this.productList = productList;
        this.cart = cart;
        this.user = user;
        this.delivererService = delivererService;
        this.adminService = adminService;
        this.registrationService = registrationService;
        this.authService = authService;
        this.employeeService = employeeService;
        this.orderService = orderService;
        this.updateCartCallback = () -> updateCartButton();
        initialize();
    }

    private void updateCartButton() {
        viewCartButton.setText("View Cart (" + cart.getTotalItems() + ")");
    }

    private void initialize() {
        setTitle("Menu - " + restaurant.getName());
        setSize(650, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        for (Product product : productList) {
            menuPanel.add(createProductPanel(product));
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        add(createBottomPanel(user), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        System.out.println("Attempting to load image for: " + restaurant.getName());
        System.out.println("Image path: " + restaurant.getImagePath());

        JPanel headerPanel = new JPanel(new BorderLayout(15, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        headerPanel.setBackground(new Color(245, 245, 245));

        ImageIcon restaurantIcon = loadImageIcon(restaurant.getImagePath());
        if (restaurantIcon != null) {
            Image scaledImage = restaurantIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            headerPanel.add(imageLabel, BorderLayout.WEST);
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 245, 245));

        JLabel nameLabel = new JLabel(restaurant.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(new Color(50, 50, 50));

        JLabel cuisineLabel = new JLabel("Cuisine: " + restaurant.getCuisineType());
        cuisineLabel.setFont(new Font("Arial", Font.PLAIN, 14));
/*
        JLabel ratingLabel = new JLabel("Rating: " + String.format("%.1f ★", restaurant.getRating()));
        ratingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ratingLabel.setForeground(new Color(255, 153, 0));
*/
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(cuisineLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        //    infoPanel.add(ratingLabel);

        headerPanel.add(infoPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createProductPanel(Product product) {
        JPanel productPanel = new JPanel(new BorderLayout(10, 10));
        productPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        productPanel.setBackground(Color.WHITE);
        productPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));

        ImageIcon productIcon = loadImageIcon(product.getImagePath());
        if (productIcon != null) {
            Image scaledImage = productIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            productPanel.add(imageLabel, BorderLayout.WEST);
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel descLabel = new JLabel(product.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(descLabel);

        productPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 120, 0));

        JButton addButton = new JButton("Add to Cart");
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            cart.addProduct(product, 1);
            JOptionPane.showMessageDialog(this, product.getName() + " added to cart!");
            viewCartButton.setText("View Cart (" + cart.getTotalItems() + ")");
        });

        rightPanel.add(priceLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(addButton);

        productPanel.add(rightPanel, BorderLayout.EAST);

        return productPanel;
    }

    private JPanel createBottomPanel(User user) {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        JButton backButton = new JButton("Back to Restaurants");
        backButton.addActionListener(e -> {
            this.dispose();
            new ClientDashboard(user, delivererService, adminService, registrationService, authService, employeeService, orderService);
        });

        viewCartButton = new JButton("View Cart (" + cart.getTotalItems() + ")");
        viewCartButton.setBackground(new Color(50, 150, 50));
        viewCartButton.setForeground(Color.WHITE);
        viewCartButton.addActionListener(e -> {
            new CartPage(cart, updateCartCallback, orderService, restaurant);
        });

        bottomPanel.add(backButton);
        bottomPanel.add(viewCartButton);



        return bottomPanel;
    }

    private ImageIcon loadImageIcon(String path) {
        if (path == null || path.isEmpty()) {
            return createDefaultIcon();
        }

        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                return new ImageIcon(imageUrl);
            }

            if (new File(path).exists()) {
                return new ImageIcon(path);
            }

            String devPath = "src/main/resources" + path;
            if (new File(devPath).exists()) {
                return new ImageIcon(devPath);
            }

            return createDefaultIcon();
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return createDefaultIcon();
        }
    }

    private ImageIcon createDefaultIcon() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 100, 100);

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String text = "No Image";
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, 50 - textWidth/2, 50);

        g.dispose();
        return new ImageIcon(image);
    }
}

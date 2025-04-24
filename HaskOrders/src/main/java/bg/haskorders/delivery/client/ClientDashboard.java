package bg.haskorders.delivery.client;

import bg.haskorders.delivery.authomation.login.LoginPage;
import bg.haskorders.delivery.model.cart.Cart;
import bg.haskorders.delivery.model.Product;
import bg.haskorders.delivery.model.restaurant.CuisineType;
import bg.haskorders.delivery.model.restaurant.Restaurant;
import bg.haskorders.delivery.model.user.User;
import bg.haskorders.delivery.repository.ProductRepository;
import bg.haskorders.delivery.repository.RestaurantRepository;
import bg.haskorders.delivery.repository.OrderRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ClientDashboard extends JFrame {
    private final User clientUser;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private Cart sharedCart;


    private JFrame frame;
    private JPanel filterPanel;
    private JPanel restaurantsPanel;
    private boolean filtersShown = false;

    private final ButtonGroup cuisineGroup = new ButtonGroup();
    private final ButtonGroup ratingGroup = new ButtonGroup();

    public ClientDashboard(User clientUser, RestaurantRepository restaurantRepository, ProductRepository productRepository) {
        this.clientUser = clientUser;
        this.sharedCart = new Cart(clientUser);
        this.restaurantRepository = RestaurantRepository.getInstance();
        this.productRepository = ProductRepository.getInstance();
        this.orderRepository = OrderRepository.getInstance();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("HaskOrders - Client Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Filters
        filterPanel = createFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.WEST);


        JButton clearFiltersButton = getJButton();

        clearFiltersButton.addActionListener(e -> clearFilters());

        JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonHolder.setBackground(filterPanel.getBackground());
        buttonHolder.add(clearFiltersButton);


        filterPanel.add(buttonHolder);

        // Restaurants list
        restaurantsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        restaurantsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        restaurantsPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(restaurantsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);
        loadRestaurants();
        frame.setVisible(true);
    }

    private void clearFilters() {
        cuisineGroup.clearSelection();
        ratingGroup.clearSelection();
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

        JButton trackOrderButton = new JButton("Track My Order");
        styleButton(trackOrderButton, new Color(33, 150, 243)); // blue
        trackOrderButton.addActionListener(e -> new OrderStatusPage(clientUser));
        buttonPanel.add(trackOrderButton);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(244, 67, 54));
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginPage(bg.haskorders.delivery.Main.userList);
        });
        buttonPanel.add(logoutButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(255, 248, 225));
        panel.setVisible(false);

        panel.add(createCuisineFilterSection());
        panel.add(createRatingFilterSection());

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

    private void toggleFilters(ActionEvent e) {
        filtersShown = !filtersShown;
        filterPanel.setVisible(filtersShown);
        frame.revalidate();
    }

    private void loadRestaurants() {
        restaurantsPanel.removeAll();

        List<Restaurant> restaurants = restaurantRepository.getAllRestaurants();

        // Apply Cuisine Filter
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

        // Apply Rating Filter
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

        // Display Restaurants
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

        // Load restaurant image
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

        JLabel ratingLabel = new JLabel(String.format("%.1f ★", restaurant.getRating()));
        ratingLabel.setForeground(new Color(255, 152, 0));

        infoPanel.add(nameLabel);
        infoPanel.add(cuisineLabel);
        infoPanel.add(ratingLabel);

        JButton viewMenuButton = new JButton("View Menu");
        styleButton(viewMenuButton, new Color(76, 175, 80));
        viewMenuButton.addActionListener(e -> {
            List<Product> products = productRepository.getProductsForRestaurant(restaurant.getRestaurant_id());
            frame.setVisible(false);
            new RestaurantMenuPage(restaurant, products, sharedCart, restaurantRepository, productRepository, clientUser);
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
            // Try loading from resources first
            java.net.URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                return scaleImageIcon(icon, 150, 100);
            }

            // Try loading from file system
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

        // Draw background
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, 150, 100);

        // Draw text
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String text = "No Image";
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, 75 - textWidth/2, 50);

        g.dispose();
        return new ImageIcon(image);
    }
}

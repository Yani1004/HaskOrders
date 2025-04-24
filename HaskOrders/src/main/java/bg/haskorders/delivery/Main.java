package bg.haskorders.delivery;

import bg.haskorders.delivery.authomation.login.LoginPage;
import bg.haskorders.delivery.model.Product;
import bg.haskorders.delivery.model.restaurant.CuisineType;
import bg.haskorders.delivery.model.restaurant.Restaurant;
import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;
import bg.haskorders.delivery.repository.ProductRepository;
import bg.haskorders.delivery.repository.RestaurantRepository;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static RestaurantRepository restaurantRepository;
    public static ProductRepository productRepository;
    public static ArrayList<User> userList;

    public static void main(String[] args) {

        // Initialize User List with a hardcoded Admin user and other users
        userList = new ArrayList<>();
        String adminPassword = "12345678";
        userList.add(User.builder()
                .username("admin")
                .password(adminPassword) // Not hashed
                .name("System Administrator")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build());
        userList.add(User.builder()
                .username("yani")
                .password("12345678")
                .address("Razlog")
                .name("System Yani")
                .email("yani@abv.bg")
                .role(Role.CLIENT)
                .build());


        // Initialize Restaurant and Product repositories
        ArrayList<Restaurant> restaurantList = new ArrayList<>();

        // Add restaurants (your favorite ones plus new ones)
        restaurantList.add(Restaurant.builder()
                .restaurant_id(1)
                .name("Burger Haven")
                .address("123 Main St")
                .phone("555-1234")
                .email("burgerhaven@example.com")
                .cuisineType(CuisineType.BURGERS)
                .rating(4.5)
                .imagePath("/images/restaurants/burger-haven-logo.jpg")
                .build());

        restaurantList.add(Restaurant.builder()
                .restaurant_id(2)
                .name("Sushi & Roll")
                .address("456 Oak Ave")
                .phone("555-5678")
                .email("sushiroll@example.com")
                .cuisineType(CuisineType.ASIAN)
                .rating(4.7)
                .imagePath("/images/restaurants/sushi-roll-logo.jpg")
                .build());

        restaurantList.add(Restaurant.builder()
                .restaurant_id(3)
                .name("Pizzaria Express")
                .address("789 Pine Blvd")
                .phone("555-9876")
                .email("pizzaria@example.com")
                .cuisineType(CuisineType.PIZZA)
                .rating(4.3)
                .imagePath("/images/restaurants/pizzaria-logo.jpg")
                .build());

        restaurantList.add(Restaurant.builder()
                .restaurant_id(4)
                .name("Sweet Tooth Bakery")
                .address("101 Dessert St")
                .phone("555-3456")
                .email("sweettreats@example.com")
                .cuisineType(CuisineType.DESERTS)
                .rating(4.8)
                .imagePath("/images/restaurants/bakery-logo.jpg")
                .build());

        restaurantRepository = new RestaurantRepository(restaurantList);

        List<Product> products = new ArrayList<>();

        products.add(Product.builder()
                .product_id(1)
                .name("Classic Cheeseburger")
                .description("Juicy beef patty with cheddar cheese")
                .price(9.99)
                .category("Burgers")
                .imagePath("/images/products/cheese-burger.jpeg")
                .restaurantID(1)
                .build());
        products.add(Product.builder()
                .product_id(2)
                .name("Bacon Double Burger")
                .description("Double patty with crispy bacon")
                .price(11.49)
                .category("Burgers")
                .imagePath("/images/products/double-bacon.jpg")
                .restaurantID(1)
                .build());

        // Products for Sushi & Roll (restaurantID = 2)
        products.add(Product.builder()
                .product_id(3)
                .name("California Roll")
                .description("Crab, avocado and cucumber")
                .price(7.99)
                .category("Sushi")
                .imagePath("/images/products/California-rolls-sushi.jpeg")
                .restaurantID(2)
                .build());
        products.add(Product.builder()
                .product_id(4)
                .name("Spicy Tuna Roll")
                .description("Tuna with spicy mayo")
                .price(8.99)
                .category("Sushi")
                .imagePath("/images/products/spicy-tuna-roll.jpg")
                .restaurantID(2)
                .build());

        // Products for Pizzaria Express (restaurantID = 3)
        products.add(Product.builder()
                .product_id(5)
                .name("Margherita Pizza")
                .description("Classic tomato sauce and mozzarella")
                .price(10.99)
                .category("Pizza")
                .imagePath("/images/products/margherita-pizza.jpg")
                .restaurantID(3)
                .build());
        products.add(Product.builder()
                .product_id(6)
                .name("Pepperoni Pizza")
                .description("Spicy pepperoni slices with mozzarella")
                .price(12.49)
                .category("Pizza")
                .imagePath("/images/products/peperoni-pizza.jpg")
                .restaurantID(3)
                .build());

        // Products for Sweet Tooth Bakery (restaurantID = 4)
        products.add(Product.builder()
                .product_id(7)
                .name("Chocolate Lava Cake")
                .description("Warm cake with gooey center")
                .price(6.49)
                .category("Desserts")
                .restaurantID(4)
                .imagePath("/images/products/lava-cake.jpg")
                .build());
        products.add(Product.builder()
                .product_id(8)
                .name("Strawberry Cheesecake")
                .description("Creamy cheesecake with strawberry glaze")
                .price(5.99)
                .category("Desserts")
                .restaurantID(4)
                .imagePath("/images/products/Strawberry-Cheesecake.jpg")
                .build());

        // Initialize ProductRepository
        productRepository = new ProductRepository(products);

        // Start the Login Page
        SwingUtilities.invokeLater(() -> new LoginPage(userList));
    }
}

package com.example.haskorders.config;

import com.example.haskorders.entities.*;
import com.example.haskorders.entities.restaurant.CuisineType;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.Role;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.ProductRepository;
import com.example.haskorders.repositories.RestaurantRepository;
import com.example.haskorders.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository, RestaurantRepository restaurantRepository, ProductRepository productRepository) {
        return args -> {
            System.out.println("Initializing admin...");
            if(userRepository.findByUsername("admin").isEmpty()) {
                System.out.println("Creating admin...");
                User admin = User.builder()
                        .name("Admin")
                        .username("admin")
                        .email("admin@haskorders.com")
                        .password("admin123")
                        .phone("0888888888")
                        .address("Top secret")
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("Admin user saves successfully");
            }
            else{
                System.out.println("Admin user already exists");
            }

            if (userRepository.findByUsername("yani").isEmpty()) {
                User yani = User.builder()
                        .username("yani")
                        .password("12345678")
                        .address("Razlog")
                        .name("System Yani")
                        .email("yani@abv.bg")
                        .role(Role.CLIENT)
                        .build();
                userRepository.save(yani);
                System.out.println("Yani user saved successfully");
            }

            if (userRepository.findByUsername("teodor").isEmpty()) {
                User teodor = User.builder()
                        .username("teodor")
                        .password("12345678")
                        .address("Razlog")
                        .name("System Teodor")
                        .email("teodor@abv.bg")
                        .role(Role.EMPLOYEE)
                        .build();
                userRepository.save(teodor);
                System.out.println("Teodor user saved successfully");
            }

            if (userRepository.findByUsername("ertaka").isEmpty()) {
                User ertaka = User.builder()
                        .username("ertaka")
                        .password("12345678")
                        .name("Erturul Test")
                        .email("erturul@dtest.com")
                        .address("Haskovo")
                        .role(Role.DELIVERER)
                        .build();
                userRepository.save(ertaka);
                System.out.println("Ertaka user saved successfully");
            }

            // Инициализация на ресторанти
            if (restaurantRepository.count() == 0) {
                Restaurant burgerHaven = restaurantRepository.save(Restaurant.builder()
                        .name("Burger Haven")
                        .address("123 Main St")
                        .phone("555-1234")
                        .email("burgerhaven@example.com")
                        .cuisineType(CuisineType.BURGERS)
                        .imagePath("/images/restaurants/burger-haven-logo.jpg")
                        .build());

                Restaurant sushiRoll = restaurantRepository.save(Restaurant.builder()
                        .name("Sushi & Roll")
                        .address("456 Oak Ave")
                        .phone("555-5678")
                        .email("sushiroll@example.com")
                        .cuisineType(CuisineType.ASIAN)
                        .imagePath("/images/restaurants/sushi-roll-logo.jpg")
                        .build());

                Restaurant pizzariaExpress = restaurantRepository.save(Restaurant.builder()
                        .name("Pizzaria Express")
                        .address("789 Pine Blvd")
                        .phone("555-9876")
                        .email("pizzaria@example.com")
                        .cuisineType(CuisineType.PIZZA)
                        .imagePath("/images/restaurants/pizzaria-logo.jpg")
                        .build());

                Restaurant sweetBakery = restaurantRepository.save(Restaurant.builder()
                        .name("Sweet Tooth Bakery")
                        .address("101 Dessert St")
                        .phone("555-3456")
                        .email("sweettreats@example.com")
                        .cuisineType(CuisineType.DESSERTS)
                        .imagePath("/images/restaurants/bakery-logo.jpg")
                        .build());

                if (productRepository.count() == 0) {
                    productRepository.saveAll(List.of(
                            // Burger Haven
                            Product.builder()
                                    .name("Classic Cheeseburger")
                                    .description("Juicy beef patty with cheddar cheese")
                                    .price(9.99)
                                    .category("Burgers")
                                    .imagePath("/images/products/cheese-burger.jpeg")
                                    .restaurant(burgerHaven)
                                    .build(),
                            Product.builder()
                                    .name("Bacon Double Burger")
                                    .description("Double patty with crispy bacon")
                                    .price(11.49)
                                    .category("Burgers")
                                    .imagePath("/images/products/double-bacon.jpg")
                                    .restaurant(burgerHaven)
                                    .build(),

                            // Sushi & Roll
                            Product.builder()
                                    .name("California Roll")
                                    .description("Crab, avocado and cucumber")
                                    .price(7.99)
                                    .category("Sushi")
                                    .imagePath("/images/products/California-rolls-sushi.jpeg")
                                    .restaurant(sushiRoll)
                                    .build(),
                            Product.builder()
                                    .name("Spicy Tuna Roll")
                                    .description("Tuna with spicy mayo")
                                    .price(8.99)
                                    .category("Sushi")
                                    .imagePath("/images/products/spicy-tuna-roll.jpg")
                                    .restaurant(sushiRoll)
                                    .build(),

                            // Pizzaria Express
                            Product.builder()
                                    .name("Margherita Pizza")
                                    .description("Classic tomato sauce and mozzarella")
                                    .price(10.99)
                                    .category("Pizza")
                                    .imagePath("/images/products/margherita-pizza.jpg")
                                    .restaurant(pizzariaExpress)
                                    .build(),
                            Product.builder()
                                    .name("Pepperoni Pizza")
                                    .description("Spicy pepperoni slices with mozzarella")
                                    .price(12.49)
                                    .category("Pizza")
                                    .imagePath("/images/products/peperoni-pizza.jpg")
                                    .restaurant(pizzariaExpress)
                                    .build(),

                            // Sweet Tooth Bakery
                            Product.builder()
                                    .name("Chocolate Lava Cake")
                                    .description("Warm cake with gooey center")
                                    .price(6.49)
                                    .category("Desserts")
                                    .imagePath("/images/products/lava-cake.jpg")
                                    .restaurant(sweetBakery)
                                    .build(),
                            Product.builder()
                                    .name("Strawberry Cheesecake")
                                    .description("Creamy cheesecake with strawberry glaze")
                                    .price(5.99)
                                    .category("Desserts")
                                    .imagePath("/images/products/Strawberry-Cheesecake.jpg")
                                    .restaurant(sweetBakery)
                                    .build()
                    ));
                }
            }
        };
    }
}

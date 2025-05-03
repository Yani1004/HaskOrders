package com.example.haskorders.services;

import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.Product;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.OrderRepository;
import com.example.haskorders.repositories.ProductRepository;
import com.example.haskorders.repositories.RestaurantRepository;
import com.example.haskorders.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    public void updateRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    public void removeRestaurant(int id) {
        restaurantRepository.deleteById(id);
    }

    public Restaurant getRestaurantById(int id) {
        return restaurantRepository.findById(id).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public boolean removeProduct(int id) {
        productRepository.deleteById(id);
        return true;
    }

    public List<Product> getProductsByRestaurant(int restaurant_id) {
        return productRepository.findByRestaurantId(restaurant_id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }
}

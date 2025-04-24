package bg.haskorders.delivery.employee;

import bg.haskorders.delivery.model.*;
import bg.haskorders.delivery.model.order.Order;
import bg.haskorders.delivery.model.order.OrderStatus;
import bg.haskorders.delivery.model.restaurant.Restaurant;
import bg.haskorders.delivery.repository.*;

import java.time.LocalDateTime;
import java.util.List;

public class EmployeeService {

    private final RestaurantRepository restaurantRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;

    public EmployeeService() {
        this.restaurantRepo = RestaurantRepository.getInstance();
        this.productRepo = ProductRepository.getInstance();
        this.orderRepo = OrderRepository.getInstance();
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepo.getAllRestaurants();
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurantRepo.addRestaurant(restaurant);
    }

    public void updateRestaurant(Restaurant restaurant) {
        restaurantRepo.updateRestaurant(restaurant);
    }

    public void removeRestaurant(int id) {
        restaurantRepo.removeRestaurant(id);
    }

    public Restaurant getRestaurantById(int id) {
        return restaurantRepo.getRestaurantById(id);
    }

    public List<Product> getAllProducts() {
        return productRepo.getAllProducts();
    }

    public void addProduct(Product product) {
        productRepo.addProduct(product);
    }

    public boolean removeProduct(int id) {
        return productRepo.removeProduct(id);
    }

    public List<Product> getProductsForRestaurant(int restaurantId) {
        return productRepo.getProductsForRestaurant(restaurantId);
    }

    public List<Order> getAllOrders() {
        return orderRepo.getAllOrders();
    }

    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        return orderRepo.updateStatus(orderId, newStatus);
    }

    public double calculateCompanyRevenue(LocalDateTime start, LocalDateTime end) {
        return orderRepo.calculateCompanyRevenue(start, end);
    }

    public double calculateDeliveryEarnings(int deliveryPersonId, LocalDateTime start, LocalDateTime end) {
        return orderRepo.calculateDeliveryEarnings(deliveryPersonId, start, end);
    }

    public boolean updateStatus(int orderId, OrderStatus newStatus) {
        return orderRepo.updateStatus(orderId, newStatus);
    }
}

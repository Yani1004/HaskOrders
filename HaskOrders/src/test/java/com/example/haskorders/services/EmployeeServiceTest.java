package com.example.haskorders.services;

import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.Product;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.OrderRepository;
import com.example.haskorders.repositories.ProductRepository;
import com.example.haskorders.repositories.RestaurantRepository;
import com.example.haskorders.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {
    private RestaurantRepository restaurantRepository;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private OrderService orderService;
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        restaurantRepository = Mockito.mock(RestaurantRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = Mockito.mock(OrderService.class);
        userRepository = Mockito.mock(UserRepository.class);
        employeeService = new EmployeeService(restaurantRepository, productRepository, orderRepository, orderService, userRepository);
    }

    @Test
    public void GetAllRestaurants() {
        List<Restaurant> mockList = List.of(new Restaurant(), new Restaurant());
        when(restaurantRepository.findAll()).thenReturn(mockList);

        List<Restaurant> result = employeeService.getAllRestaurants();

        assertEquals(2, result.size());
        verify(restaurantRepository).findAll();
    }

    @Test
    public void AddRestaurant() {
        Restaurant restaurant = new Restaurant();
        employeeService.addRestaurant(restaurant);

        verify(restaurantRepository).save(restaurant);
    }

    @Test
    public void UpdateRestaurant() {
        Restaurant restaurant = new Restaurant();
        employeeService.updateRestaurant(restaurant);

        verify(restaurantRepository).save(restaurant);
    }

    @Test
    public void RemoveRestaurant() {
        employeeService.removeRestaurant(1);

        verify(restaurantRepository).deleteById(1);
    }

    @Test
    public void GetRestaurantById_found() {
        Restaurant r = new Restaurant();
        when(restaurantRepository.findById(5)).thenReturn(Optional.of(r));

        Restaurant result = employeeService.getRestaurantById(5);

        assertNotNull(result);
        verify(restaurantRepository).findById(5);
    }

    @Test
    public void GetRestaurantById_notFound() {
        when(restaurantRepository.findById(5)).thenReturn(Optional.empty());

        Restaurant result = employeeService.getRestaurantById(5);

        assertNull(result);
    }

    @Test
    public void GetAllProducts() {
        List<Product> mockProducts = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(mockProducts);

        List<Product> result = employeeService.getAllProducts();

        assertEquals(2, result.size());
        verify(productRepository).findAll();
    }

    @Test
    public void AddProduct() {
        Product product = new Product();
        employeeService.addProduct(product);

        verify(productRepository).save(product);
    }

    @Test
    public void RemoveProduct() {
        boolean result = employeeService.removeProduct(7);

        assertTrue(result);
        verify(productRepository).deleteById(7);
    }

    @Test
    public void GetProductsByRestaurant() {
        List<Product> products = List.of(new Product());
        when(productRepository.findByRestaurantId(3)).thenReturn(products);

        List<Product> result = employeeService.getProductsByRestaurant(3);

        assertEquals(1, result.size());
        verify(productRepository).findByRestaurantId(3);
    }

    @Test
    public void GetAllOrders() {
        List<Order> orders = List.of(new Order(), new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = employeeService.getAllOrders();

        assertEquals(3, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    public void GetUserById_found() {
        User user = new User();
        when(userRepository.findById(10)).thenReturn(Optional.of(user));

        User result = employeeService.getUserById(10);

        assertNotNull(result);
        verify(userRepository).findById(10);
    }

    @Test
    public void GetUserById_notFound() {
        when(userRepository.findById(10)).thenReturn(Optional.empty());

        User result = employeeService.getUserById(10);

        assertNull(result);
    }
}

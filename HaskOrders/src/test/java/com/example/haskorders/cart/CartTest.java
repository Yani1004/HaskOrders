package com.example.haskorders.cart;

import com.example.haskorders.client.cart.Cart;
import com.example.haskorders.client.cart.CartItem;
import com.example.haskorders.client.cart.MemoryCartStorage;
import com.example.haskorders.entities.*;
import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.order.Order_status;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {
    private Cart cart;
    private Product product1;
    private Product product2;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        cart = new Cart(testUser, new MemoryCartStorage());

        product1 = Product.builder().id(1).name("Pizza").price(10.0).build();
        product2 = Product.builder().id(2).name("Pasta").price(7.5).build();
    }

    @Test
    public void AddProduct_NewProduct() {
        cart.addProduct(product1, 2);

        List<CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        assertEquals(2, items.get(0).getQuantity());
    }

    @Test
    public void AddProduct_ExistingProduct() {
        cart.addProduct(product1, 1);
        cart.addProduct(product1, 2); // should increase quantity

        List<CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        assertEquals(3, items.get(0).getQuantity());
    }

    @Test
    public void RemoveItem() {
        cart.addProduct(product1, 1);
        cart.removeItem(0);

        assertTrue(cart.isEmpty());
    }

    @Test
    public void UpdateQuantity() {
        cart.addProduct(product1, 1);
        cart.updateQuantity(0, 5);

        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void GetTotalPrice() {
        cart.addProduct(product1, 2); // 2 x 10 = 20
        cart.addProduct(product2, 1); // 1 x 7.5 = 7.5

        assertEquals(27.5, cart.getTotalPrice(), 0.001);
    }

    @Test
    public void GetTotalItems() {
        cart.addProduct(product1, 2);
        cart.addProduct(product2, 3);

        assertEquals(5, cart.getTotalItems());
    }

    @Test
    public void IsEmpty() {
        assertTrue(cart.isEmpty());
        cart.addProduct(product1, 1);
        assertFalse(cart.isEmpty());
    }

    @Test
    public void Clear() {
        cart.addProduct(product1, 2);
        cart.clear();

        assertTrue(cart.isEmpty());
    }

    @Test
    public void CreateOrder() {
        cart.addProduct(product1, 2); // 2 x 10 = 20
        cart.addProduct(product2, 1); // 1 x 7.5 = 7.5

        Restaurant restaurant = new Restaurant();
        User customer = new User();
        User deliverer = new User();

        Order order = cart.createOrder(restaurant, customer, deliverer, "ул. Стара планина 42");

        assertEquals(2, order.getItems().size());
        assertEquals(27.5, order.getTotal_price(), 0.001);
        assertEquals(Order_status.PROCESSING, order.getStatus());
        assertEquals("ул. Стара планина 42", order.getDelivery_address());
        assertEquals(customer, order.getCustomer());
        assertEquals(deliverer, order.getDeliverer());
    }
}

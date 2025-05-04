package com.example.haskorders.client.cart;

import com.example.haskorders.entities.*;
import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.order.Order_status;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private final CartStorage storage;
    private final User user;

    public Cart(User user, CartStorage storage) {
        this.user = user;
        this.storage = storage;
    }

    // Получаваме артикулите в количката
    public List<CartItem> getItems() {
        return storage.getItems();
    }

    // Добавяме продукт в количката
    public void addProduct(Product product, int quantity) {
        List<CartItem> items = storage.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            if (item.getProduct().getId() == product.getId()) {
                storage.updateItemQuantity(i, item.getQuantity() + quantity);
                return;
            }
        }
        storage.addItem(new CartItem(product, quantity));
    }

    // Премахваме артикул от количката
    public void removeItem(int index) {
        storage.removeItem(index);
    }

    // Обновяваме количеството на артикул
    public void updateQuantity(int index, int newQuantity) {
        storage.updateItemQuantity(index, newQuantity);
    }

    // Общо за всички продукти в количката
    public double getTotalPrice() {
        return storage.getItems().stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    // Общо количество на продуктите в количката
    public int getTotalItems() {
        return storage.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    // Проверяваме дали количката е празна
    public boolean isEmpty() {
        return storage.getItems().isEmpty();
    }

    // Изчистваме количката
    public void clear() {
        storage.clear();
    }

    // Прехвърляме количката към поръчка
    public Order createOrder(Restaurant restaurant, User customer, User deliverer, String deliveryAddress) {
        Order order = Order.builder()
                .restaurant(restaurant)
                .date(LocalDateTime.now())
                .status(Order_status.PROCESSING)
                .customer(customer)
                .deliverer(deliverer)
                .delivery_address(deliveryAddress)
                .items(new ArrayList<>())
                .total_price(0.0)
                .build();

        double totalPrice = 0.0;

        for (CartItem cartItem : getItems()) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice(),
                    order,
                    cartItem.getProduct()
            );
            order.getItems().add(orderItem);
            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        order.setTotal_price(totalPrice);
        return order;
    }
}

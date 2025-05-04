package com.example.haskorders.client.cart;

import java.util.List;

public interface CartStorage {
    List<CartItem> getItems();
    void addItem(CartItem item);
    void updateItemQuantity(int index, int quantity);
    void removeItem(int index);
    void clear();
}

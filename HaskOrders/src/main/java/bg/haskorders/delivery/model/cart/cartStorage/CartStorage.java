package bg.haskorders.delivery.model.cart.cartStorage;

import bg.haskorders.delivery.model.cart.CartItem;

import java.util.List;

public interface CartStorage {
    void addItem(CartItem item);
    void removeItem(int index);
    void updateItem(int index, CartItem newItem);
    List<CartItem> getItems();
    void clear();
}
package bg.haskorders.delivery.repository.cartRepo;

import bg.haskorders.delivery.model.cart.CartItem;

import java.util.List;

public interface CartStorage {
    List<CartItem> getItems();
    void addItem(CartItem item);
    void updateItemQuantity(int index, int quantity);
    void removeItem(int index);
    void clear();
}


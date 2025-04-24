package bg.haskorders.delivery.model.cart;

import java.util.List;

public interface CartStorage {
    void addItem(CartItem item);
    void removeItem(int index);
    void updateItem(int index, CartItem newItem);
    List<CartItem> getItems();
    void clear();
}
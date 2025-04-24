package bg.haskorders.delivery.model.cart.cartStorage;

import bg.haskorders.delivery.model.cart.CartItem;

import java.util.ArrayList;
import java.util.List;

public class MemoryCartStorage implements CartStorage {
    private final List<CartItem> items = new ArrayList<>();

    @Override
    public void addItem(CartItem item) {
        items.add(item);
    }

    @Override
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    @Override
    public void updateItem(int index, CartItem newItem) {
        if (index >= 0 && index < items.size()) {
            items.set(index, newItem);
        }
    }

    @Override
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public void clear() {
        items.clear();
    }
}

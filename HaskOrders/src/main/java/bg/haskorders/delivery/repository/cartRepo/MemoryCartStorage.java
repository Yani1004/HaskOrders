package bg.haskorders.delivery.repository.cartRepo;

import bg.haskorders.delivery.model.cart.CartItem;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class MemoryCartStorage implements CartStorage {
    private final List<CartItem> items = new ArrayList<>();

    @Override
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public void addItem(CartItem item) {
        items.add(item);
    }

    @Override
    public void updateItemQuantity(int index, int quantity) {
        if (index >= 0 && index < items.size()) {
            items.get(index).setQuantity(quantity);
        }
    }

    @Override
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    @Override
    public void clear() {
        items.clear();
    }
}


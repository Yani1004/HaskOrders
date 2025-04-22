package bg.haskorders.delivery.model.cart;

import bg.haskorders.delivery.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Cart {
    private final CartStorage storage;
    public int getTotalItems() {
        return storage.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }


    public Cart() {
        this(new MemoryCartStorage());
    }

    public Cart(CartStorage storage) {
        this.storage = storage;
    }

    public void addProduct(Product product, int quantity) {
        // Проверка дали продуктът вече е в количката
        for (int i = 0; i < storage.getItems().size(); i++) {
            CartItem item = storage.getItems().get(i);
            if (item.getProduct().equals(product)) {
                CartItem updatedItem = new BasicCartItem(
                        product,
                        item.getQuantity() + quantity
                );
                storage.updateItem(i, updatedItem);
                return;
            }
        }
        // Ако продуктът не е в количката, добавяме го
        storage.addItem(new BasicCartItem(product, quantity));
    }

    public void removeItem(int index) {
        storage.removeItem(index);
    }

    public void updateQuantity(int index, int newQuantity) {
        List<CartItem> items = storage.getItems();
        if (index >= 0 && index < items.size() && newQuantity > 0) {
            CartItem item = items.get(index);
            storage.updateItem(index, new BasicCartItem(
                    item.getProduct(),
                    newQuantity
            ));
        }
    }

    public void clear() {
        storage.clear();
    }

    public double getTotalPrice() {
        return storage.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public List<CartItem> getItems() {
        return storage.getItems();
    }

    public boolean isEmpty() {
        return storage.getItems().isEmpty();
    }

    public int getItemCount() {
        return storage.getItems().size();
    }

}
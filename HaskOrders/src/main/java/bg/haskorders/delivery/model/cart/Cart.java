package bg.haskorders.delivery.model.cart;

import bg.haskorders.delivery.model.Product;
import bg.haskorders.delivery.model.user.User;
import bg.haskorders.delivery.repository.cartRepo.CartStorage;
import lombok.Data;

import java.util.List;


@Data
public class Cart {
    private final CartStorage storage;
    private final User user;

    public Cart(User user, CartStorage storage) {
        this.user = user;
        this.storage = storage;
    }

    public List<CartItem> getItems() {
        return storage.getItems();
    }

    public void addProduct(Product product, int quantity) {
        List<CartItem> items = storage.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            if (item.getProduct().getProduct_id() == product.getProduct_id()) {
                storage.updateItemQuantity(i, item.getQuantity() + quantity);
                return;
            }
        }
        storage.addItem(new CartItem(product, quantity));
    }

    public void removeItem(int index) {
        storage.removeItem(index);
    }

    public void updateQuantity(int index, int newQuantity) {
        storage.updateItemQuantity(index, newQuantity);
    }

    public double getTotalPrice() {
        return storage.getItems().stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public int getTotalItems() {
        return storage.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public boolean isEmpty() {
        return storage.getItems().isEmpty();
    }

    public void clear() {
        storage.clear();
    }
}

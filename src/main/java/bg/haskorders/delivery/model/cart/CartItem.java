package bg.haskorders.delivery.model.cart;

import bg.haskorders.delivery.model.Product;
import lombok.Setter;

public class CartItem {
    private final Product product;
    @Setter
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public CartItem(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}
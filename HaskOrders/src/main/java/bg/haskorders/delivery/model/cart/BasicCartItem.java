package bg.haskorders.delivery.model.cart;

import bg.haskorders.delivery.model.Product;

public class BasicCartItem implements CartItem {
    private final Product product;
    private int quantity;

    public BasicCartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public Product getProduct() {
        return product;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}

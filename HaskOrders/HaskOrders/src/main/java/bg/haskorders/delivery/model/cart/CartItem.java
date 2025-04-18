package bg.haskorders.delivery.model.cart;

import bg.haskorders.delivery.model.Product;

public interface CartItem {
    Product getProduct();
    int getQuantity();
    void setQuantity(int quantity);
    double getTotalPrice();
}


package bg.haskorders.delivery.model.order;
import java.time.LocalDateTime;
import java.util.List;

import bg.haskorders.delivery.model.Product;
import bg.haskorders.delivery.model.cart.CartItem;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class Order {
    private Integer order_id;
    private Integer user_id;
    private List<Product> productList;
    private List<CartItem> cartItems;
    private Integer restaurant_id;
    private Integer delivery_person_id;
    private LocalDateTime order_date;
    private String delivery_address;
    private OrderStatus status;
    private double total_amount;
}

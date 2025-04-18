package bg.haskorders.delivery.model.order;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {
    private Integer order_id;
    private Integer user_id;
    private Integer restaurant_id;
    private Integer delivery_person_id;
    private LocalDateTime order_date;
    private String delivery_address;
    private OrderStatus status;
    private double total_amount;
}

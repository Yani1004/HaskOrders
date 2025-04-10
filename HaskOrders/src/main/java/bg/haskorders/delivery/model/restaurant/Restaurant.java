package bg.haskorders.delivery.model.restaurant;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Restaurant {
    private Integer restaurant_id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private CuisineType cuisineType;
}
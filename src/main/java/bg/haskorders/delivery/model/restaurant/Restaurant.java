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
    private double rating;
    private String imagePath; // Add this field for image storage

    // You can add this method to get a default image if none is specified
    public String getImagePath() {
        return imagePath != null ? imagePath : "/images/default_restaurant.png";
    }
}
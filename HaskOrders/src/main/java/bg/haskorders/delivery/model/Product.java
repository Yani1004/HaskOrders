package bg.haskorders.delivery.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product
{
    // конструктори гетъри сетъри на класа продукти сложен метод за взимане на информация като текст
    private Integer product_id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Integer restaurantID;

    private String imagePath; // Add this field for image storage

    // You can add this method to get a default image if none is specified
    public String getImagePath() {
        return imagePath != null ? imagePath : "/images/default_restaurant.png";
    }
}
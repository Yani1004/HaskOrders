public class Product
{
    // конструктори гетъри сетъри на класа продукти сложен метод за взимане на информация като текст
    private Integer product_id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Integer restaurantID;

    public Product(String name, String description, Double price, String category, Integer restaurantID) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.restaurantID = restaurantID;
    }

    public Product(Integer product_id, String name, String description, Double price, String category, Integer restaurantID) {
        this.product_id = product_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.restaurantID = restaurantID;
    }

    public Product() {
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRestaurantID(Integer restaurantID) {
        this.restaurantID = restaurantID;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public Integer getRestaurantID() {
        return restaurantID;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %.2f лв.", name, category, price);
    }


}

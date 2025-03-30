public class Restaurant {
    private Integer restaurant_id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private CuisineType cuisineType;

    public Restaurant(){

    }

    public Restaurant(String name, String address, String phone, String email, CuisineType cuisineType){
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.cuisineType = cuisineType;
    }

    public Restaurant(Integer restaurant_id, String name, String address, String phone, String email, CuisineType cuisineType){
        this.restaurant_id = restaurant_id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.cuisineType = cuisineType;
    }

    public Integer getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(Integer restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CuisineType getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(CuisineType cuisineType) {
        this.cuisineType = cuisineType;
    }

    public enum CuisineType{
        BURGERS, ASIAN, PIZZA, AMERICA, FAST_FOOD, SANDWICH, SUSHI, GRILL, INTERNATIONAL, ITALIAN, SEAFOOD, BREAKFAST, VEGETARIAN, VEGAN, SWEETS, DESERTS, MEXICAN, INDIAN, MEDITERRANEAN
    }

    @Override
    public String toString(){
        return String.format("%s (%s) - %s", name, cuisineType, address);
    }
}


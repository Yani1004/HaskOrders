import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestaurantRepository {
    private final List<Restaurant> restaurants;

    public RestaurantRepository(List<Restaurant> restaurants){this.restaurants = restaurants;}

    public synchronized void addRestaurant(Restaurant restaurant){restaurants.add(restaurant);}

    public synchronized  boolean removeRestaurant(int restaurantID) {
        return restaurants.removeIf(restaurant -> restaurant.getRestaurant_id() == restaurantID);
    }

    public synchronized List<Restaurant> getRestaurantsByCuisine(Restaurant.CuisineType cuisineType){
        return restaurants.stream().filter(restaurant -> restaurant.getCuisineType()== cuisineType).toList();
    }

    public synchronized Restaurant getRestaurantById(int restaurantId){
        for (Restaurant r : restaurants){
            if (r.getRestaurant_id() != null && r.getRestaurant_id().equals(restaurantId)){
                return r;
            }
        }
        return null;
    }

    public synchronized List<Restaurant> getAllRestaurants() {
        return Collections.unmodifiableList(new ArrayList<>(restaurants));
    }

}

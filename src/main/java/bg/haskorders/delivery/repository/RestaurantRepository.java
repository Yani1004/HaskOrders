package bg.haskorders.delivery.repository;

import bg.haskorders.delivery.model.restaurant.CuisineType;
import bg.haskorders.delivery.model.restaurant.Restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RestaurantRepository {
    private static RestaurantRepository instance;
    private final List<Restaurant> restaurants;

    public RestaurantRepository() {
        this.restaurants = new ArrayList<>();
    }

    public static synchronized RestaurantRepository getInstance() {
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance ;
    }

    public synchronized void addRestaurant(Restaurant restaurant){restaurants.add(restaurant);}

    public synchronized void removeRestaurant(int restaurantID) {
        restaurants.removeIf(restaurant -> restaurant.getRestaurant_id() == restaurantID);
    }

    public synchronized List<Restaurant> getRestaurantsByCuisine(CuisineType cuisineType){
        return restaurants.stream()
                .filter(restaurant -> restaurant.getCuisineType() == cuisineType)
                .toList();
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
        return (restaurants != null) ? Collections.unmodifiableList(new ArrayList<>(restaurants)) : Collections.emptyList();
    }

    public void updateRestaurant(Restaurant updated) {
        for (int i = 0; i < restaurants.size(); i++) {
            if (Objects.equals(restaurants.get(i).getRestaurant_id(), updated.getRestaurant_id())) {
                restaurants.set(i, updated);
                return;
            }
        }
    }
}
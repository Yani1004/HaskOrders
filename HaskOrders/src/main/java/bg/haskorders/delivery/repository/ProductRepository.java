package bg.haskorders.delivery.repository;

import bg.haskorders.delivery.model.Product;
import bg.haskorders.delivery.model.restaurant.Restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProductRepository {
    private final List<Product> products;

    // Constructor with null safety check
    public ProductRepository(List<Product> products) {
        if (products == null) {
            this.products = new ArrayList<>();
        } else {
            this.products = products;
        }
    }

    // Synchronized methods to ensure thread safety
    public synchronized void addProduct(Product product) {
        if (product != null) {
            products.add(product);
        }
    }

    public synchronized boolean removeProduct(int productID) {
        return products.removeIf(product -> product.getProduct_id() == productID);
    }

    public synchronized List<Product> getProductsForRestaurant(int restaurantID) {
        return getProductByRestaurant(restaurantID);
    }

    public synchronized List<Product> getProductByCategory(String category) {
        List<Product> categorizedProducts = new ArrayList<>();
        if (category != null) {
            for (Product p : products) {
                if (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category)) {
                    categorizedProducts.add(p);
                }
            }
        }
        return categorizedProducts;
    }

    public synchronized List<Product> getProductByRestaurant(int restaurantID) {
        List<Product> sortedProducts = new ArrayList<>();
        if (restaurantID > 0) {
            for (Product p : products) {
                if (p.getRestaurantID() != null && p.getRestaurantID().equals(restaurantID)) {
                    sortedProducts.add(p);
                }
            }
        }
        return sortedProducts;
    }

    public synchronized List<Product> getAllProducts() {
        return Collections.unmodifiableList(new ArrayList<>(products));
    }
    public List<Product> findByRestaurant(Long restaurantId) {
        List<Product> restaurantProducts = new ArrayList<>();

        if (restaurantId != null && products != null) {
            for (Product p : products) {
                if (restaurantId.equals(p.getRestaurantID())) {
                    restaurantProducts.add(p);
                }
            }
        }
        return restaurantProducts;
    }
}

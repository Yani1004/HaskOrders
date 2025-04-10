package bg.haskorders.delivery.repository;

import bg.haskorders.delivery.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductRepository
// izpipano thread safety ;)
{
    private final List<Product> products;

    public ProductRepository(List<Product> products) {
        this.products = products;
    }

    public synchronized void addProduct(Product product)
    {
        products.add(product);
    }

    public synchronized  boolean removeProduct(int productID) {
        return products.removeIf(product -> product.getProduct_id() == productID);
    }

    public synchronized  List<Product> getProductByCategory(String Category)
    {
        List<Product> categorizedproducts = new ArrayList<>();
        if (Category != null)
        {
            for (Product p : products) {
                if (p.getCategory() != null && p.getCategory().equalsIgnoreCase(Category)) {
                    categorizedproducts.add(p);
                }
            }
        }
        return categorizedproducts;

    }

    public synchronized List<Product> getProductByRestaurant(int restaurantID)
    {
        List<Product> sortedProducts = new ArrayList<>();
        for (Product p : products) {
            if (p.getRestaurantID().equals(restaurantID)) {
                sortedProducts.add(p);
            }
        }
        return sortedProducts;

    }

    public synchronized List<Product> getAllProducts() {
        return Collections.unmodifiableList(new ArrayList<>(products));
    }

}
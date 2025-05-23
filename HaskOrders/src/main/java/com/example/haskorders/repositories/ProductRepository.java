package com.example.haskorders.repositories;

import com.example.haskorders.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);
    List<Product> findByRestaurantId(int restaurant_id);
    List<Product> findByCategory(String category);
    List<Product> findByPriceLessThanEqual(double price);
}

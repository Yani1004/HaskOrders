package com.example.haskorders.entities;

import com.example.haskorders.entities.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, length = 50)
    private String description;

    @ManyToOne()
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column()
    private String imagePath; // Add this field for image storage

    // You can add this method to get a default image if none is specified
    public String getImagePath() {
        return imagePath != null ? imagePath : "/images/default_restaurant.png";
    }

    public Product(String name, String description, double price, String category, String imagePath, Restaurant restaurant) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
        this.restaurant = restaurant;
    }

    public Product(String name, String description, double price, String category, Restaurant restaurant) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.restaurant = restaurant;
    }
}

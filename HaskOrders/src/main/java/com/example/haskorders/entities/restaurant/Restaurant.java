package com.example.haskorders.entities.restaurant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(unique = true, nullable = false, length = 10)
    private String phone;

    @Column(unique = true, nullable = false, length = 100)
    private String address;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private CuisineType cuisineType;

    @Column(nullable = false)
    private double rating;

    @Column()
    private String imagePath;

    public Restaurant(String name, String phone, String address, String email, CuisineType cuisineType, double rating, String imagePath) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.cuisineType = cuisineType;
        this.rating = rating;
        this.imagePath = imagePath;
    }

    // You can add this method to get a default image if none is specified
    public String getImagePath() {
        return imagePath != null ? imagePath : "/images/default_restaurant.png";
    }
}

package com.example.haskorders.repositories;

import com.example.haskorders.entities.order.Order;
import com.example.haskorders.entities.order.Order_status;
import com.example.haskorders.entities.restaurant.Restaurant;
import com.example.haskorders.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomer(User customer);
    List<Order> findByRestaurant(Restaurant restaurant);
    List<Order> findByStatus(Order_status status);
    List<Order> findByDelivererAndStatus(User deliverer, Order_status status);
    List<Order> findByDelivererAndStatusAndDateBetween(User deliverer, Order_status status, LocalDateTime from, LocalDateTime to);
}

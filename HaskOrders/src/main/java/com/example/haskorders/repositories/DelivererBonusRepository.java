package com.example.haskorders.repositories;

import com.example.haskorders.entities.DelivererBonus;
import com.example.haskorders.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DelivererBonusRepository extends JpaRepository<DelivererBonus, Integer> {
    Optional<DelivererBonus> findByDeliverer(User deliverer);
}

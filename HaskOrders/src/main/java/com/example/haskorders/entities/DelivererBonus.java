package com.example.haskorders.entities;

import com.example.haskorders.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deliverer_bonuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelivererBonus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private double totalRevenue;


    @Column
    private int bonus_count;

    @OneToOne
    @JoinColumn(name = "deliverer_id")
    private User deliverer;

    public DelivererBonus(double totalRevenue, int bonus_count, User deliverer) {
        this.totalRevenue = totalRevenue;
        this.bonus_count = bonus_count;
        this.deliverer = deliverer;
    }
}

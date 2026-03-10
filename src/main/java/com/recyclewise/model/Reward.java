package com.recyclewise.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A benefit/reward that users can redeem using their points.
 * Provided by local government — food, transport, bill discounts.
 */
@Entity
@Table(name = "rewards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int pointsCost;

    @Column(nullable = false)
    private String category; // FOOD_DRINKS, TRANSPORT, BILLS

    private String icon;

    private String provider; // e.g. "City Government", "TransJakarta"

    @Column(nullable = false)
    @Builder.Default
    private boolean available = true;

    @Column(nullable = false)
    @Builder.Default
    private int stockRemaining = 100;
}

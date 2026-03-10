package com.recyclewise.service;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Encapsulates all points calculation logic.
 *
 * SOLID — (S) SRP: ONLY responsible for computing points — nothing else.
 * SOLID — (O) OCP: add new categories by extending the multiplier map, not editing logic.
 * OOP   — Encapsulation: multiplier table is private and accessed only via calculatePoints()
 *
 * Formula: points = weight(kg) × categoryMultiplier
 */
@Component
public class PointsCalculator {

    // Points per kg for each waste category
    private static final Map<String, Integer> MULTIPLIERS = Map.of(
        "RECYCLABLE", 15,   // most encouraged
        "ORGANIC",    10,
        "HAZARDOUS",  20,   // requires special handling — rewarded most
        "GENERAL",     5    // least preferred
    );

    public int calculatePoints(String category, double weightKg) {
        int multiplier = MULTIPLIERS.getOrDefault(category.toUpperCase(), 5);
        return (int) Math.round(weightKg * multiplier);
    }

    public Map<String, Integer> getMultipliers() {
        return MULTIPLIERS;
    }
}

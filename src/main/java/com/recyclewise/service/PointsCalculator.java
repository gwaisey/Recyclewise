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
        "PAPER",       15,
        "RECYCLABLES", 15,   // Plastics, Metals, Glass
        "ORGANIC",     10,
        "HAZARDOUS",   20,   
        "GENERAL",      5    
    );

    public int calculatePoints(String category, double weightKg) {
        int multiplier = MULTIPLIERS.getOrDefault(category.toUpperCase(), 5);
        double rawPoints = weightKg * multiplier;
        
        // Sanity cap to prevent any mathematical overflow before casting to int
        // Current max per submission is 5000, but we cap at a safe logic boundary
        double cappedPoints = Math.min(rawPoints, 10000.0);
        
        return (int) Math.round(cappedPoints);
    }

    public Map<String, Integer> getMultipliers() {
        return MULTIPLIERS;
    }
}

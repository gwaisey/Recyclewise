package com.recyclewise.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * One line item in a trash submission — a specific category + weight.
 * Points = weight (kg) × category multiplier.
 *
 * Category multipliers:
 *   RECYCLABLE = 15 pts/kg  (highest — most valuable)
 *   ORGANIC    = 10 pts/kg
 *   HAZARDOUS  = 20 pts/kg  (highest — requires special handling)
 *   GENERAL    =  5 pts/kg  (lowest — least preferred)
 */
@Entity
@Table(name = "submission_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private TrashSubmission submission;

    @Column(nullable = false)
    private String category; // RECYCLABLE, ORGANIC, HAZARDOUS, GENERAL

    @Column(nullable = false)
    private double weightKg;

    @Column(nullable = false)
    private int pointsEarned; // calculated at submission time
}

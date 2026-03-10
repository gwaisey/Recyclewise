package com.recyclewise.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a single trash submission by a user at a station.
 * Points are calculated from weight × category multiplier.
 *
 * SOLID — (S) SRP: tracks only submission data and calculated points
 */
@Entity
@Table(name = "trash_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrashSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id", nullable = false)
    private TrashStation station;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SubmissionItem> items;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private int pointsEarned = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;

    public enum SubmissionStatus {
        PENDING,    // submitted, awaiting station confirmation
        CONFIRMED,  // station staff confirmed trash received
        REJECTED    // rejected (wrong category, contaminated, etc.)
    }
}

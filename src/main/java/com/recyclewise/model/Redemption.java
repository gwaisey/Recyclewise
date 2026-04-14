package com.recyclewise.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records a user's reward redemption event.
 * SOLID — (S) SRP: only tracks redemption history
 */
@Entity
@Table(name = "redemptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Redemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(nullable = false)
    private int pointsSpent;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime redeemedAt = LocalDateTime.now();

    @Column(nullable = false, unique = true)
    private String voucherCode; // auto-generated unique code

    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RedemptionStatus status = RedemptionStatus.ACTIVE;

    public enum RedemptionStatus { ACTIVE, USED, EXPIRED }
}

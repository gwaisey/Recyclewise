package com.recyclewise.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a RecycleWise staff member with admin access.
 * SOLID (S) - SRP: only represents admin identity and role
 */
@Entity
@Table(name = "admin_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminRole role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id")
    private TrashStation assignedStation; // null for SUPER_ADMIN

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    public enum AdminRole {
        SUPER_ADMIN, STATION_STAFF
    }
}

package com.recyclewise.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an organized trash station located around town.
 * Users select one of these when submitting their trash.
 */
@Entity
@Table(name = "trash_stations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrashStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String district; // area/neighborhood name

    private String operatingHours; // e.g. "Mon-Sat 08:00-17:00"

    private String icon; // emoji for map display

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    // GPS coordinates for map display
    private Double latitude;
    private Double longitude;
}

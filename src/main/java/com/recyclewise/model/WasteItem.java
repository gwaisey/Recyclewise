package com.recyclewise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "waste_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; // RECYCLABLE, ORGANIC, HAZARDOUS, GENERAL

    @Column(length = 1000)
    private String disposalInstructions;

    @Column(length = 500)
    private String tips;

    private String icon; // emoji or icon class

    private boolean recyclable;

    private String binColor; // green, blue, yellow, red, black
}

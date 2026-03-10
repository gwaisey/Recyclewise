package com.recyclewise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recycling_tips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecyclingTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String category; // REDUCE, REUSE, RECYCLE, COMPOST

    private String icon;

    private int impactScore; // 1-10 environmental impact
}

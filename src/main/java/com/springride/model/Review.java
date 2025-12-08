package com.springride.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Review.java → Avis après trajet
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rating; // Note de 1 à 5

    private String comment; // "Très sympa", "Ponctuel", etc.

    @ManyToOne(fetch = FetchType.LAZY)
    private User reviewer; // Qui laisse l’avis
    @ManyToOne(fetch = FetchType.LAZY)
    private User reviewed; // Qui reçoit l’avis
    @ManyToOne(fetch = FetchType.LAZY)
    private Trip trip; // Sur quel trajet
}

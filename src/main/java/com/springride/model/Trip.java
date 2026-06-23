package com.springride.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.springride.model.enums.TripStatus;

// Trip.java → Le trajet proposé
@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String departureCity; // ex: Casablanca
    @Column(nullable = false)
    private String arrivalCity; // ex: Rabat

    @Column(nullable = false)
    private LocalDateTime departureDateTime; // Date et heure de départ

    @Column(nullable = false)
    private int availableSeats; // Combien de places libres

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal pricePerSeat; // Prix par place (ex: 120.00 DH)

    @Column(nullable = false)
    private Integer estimatedDuration; // Durée estimée en minutes

    private String description; // "Je passe par Salé", "Musique autorisée", etc.

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private TripStatus status = TripStatus.PLANIFIE;

    // Qui conduit ?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    // Quelle voiture ?
    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Review> reviews = new HashSet<>();
}

package com.springride.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private String description; // "Je passe par Salé", "Musique autorisée", etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.PLANIFIE;

    // Qui conduit ?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    // Quelle voiture ?
    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
}

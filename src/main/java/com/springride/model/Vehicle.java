// Vehicle.java → La voiture du conducteur

package com.springride.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand; // ex: Renault, Peugeot
    @Column(nullable = false)
    private String model; // ex: Clio, 308
    @Column(nullable = false)
    private String color;
    @Column(nullable = false, unique = true)
    private String licensePlate; // Immatriculation unique
    @Builder.Default
    private int seats = 4; // Nombre de places totales

    // Une voiture appartient à un seul propriétaire
    @ManyToOne(fetch = FetchType.LAZY) // Chargement paresseux = plus rapide
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
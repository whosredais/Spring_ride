package com.springride.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    // Infos utilisateur
    private UserResponse userProfile;

    // Statistiques
    private int totalTripsAsDriver;
    private int totalTripsAsPassenger;
    private double averageRating;
    private java.math.BigDecimal estimatedRevenue;

    // Listes (récentes)
    private List<TripResponse> recentTripsPublished;
    private List<ReservationResponse> recentReservationsRequest;

    // Avertissements
    private Integer strikes;
    private String warningMessage;
}

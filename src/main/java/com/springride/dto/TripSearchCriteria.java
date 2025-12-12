package com.springride.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSearchCriteria {
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime date; // Pour chercher par jour (ignorer l'heure)
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minSeats; // optionnel
}

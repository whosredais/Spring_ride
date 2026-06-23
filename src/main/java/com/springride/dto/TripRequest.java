package com.springride.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripRequest {

    @NotBlank(message = "Ville de départ obligatoire")
    private String departureCity;

    @NotBlank(message = "Ville d'arrivée obligatoire")
    private String arrivalCity;

    @NotNull(message = "Date de départ obligatoire")
    @Future(message = "La date doit être dans le futur")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime departureDateTime;

    @Min(value = 1, message = "Au moins 1 place")
    private int availableSeats;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private BigDecimal pricePerSeat;

    private String description;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 15, message = "La durée minimale est de 15 minutes")
    private Integer estimatedDuration;

    @NotNull(message = "ID du véhicule obligatoire")
    private Long vehicleId;
}

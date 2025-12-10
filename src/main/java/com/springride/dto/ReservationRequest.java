package com.springride.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    @NotNull(message = "L'ID du trajet est obligatoire")
    private Long tripId;

    @Min(value = 1, message = "Au moins 1 place")
    private int seatsRequested;
}

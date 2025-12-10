package com.springride.dto;

import jakarta.validation.constraints.Max;
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
public class ReviewRequest {

    @NotNull(message = "L'ID du trajet est obligatoire")
    private Long tripId;

    @NotNull(message = "L'ID de l'utilisateur noté est obligatoire")
    private Long reviewedUserId;

    @Min(value = 1, message = "La note doit être entre 1 et 5")
    @Max(value = 5, message = "La note doit être entre 1 et 5")
    private int rating;

    private String comment;
}

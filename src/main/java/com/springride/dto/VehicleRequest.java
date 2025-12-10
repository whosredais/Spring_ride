package com.springride.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRequest {

    @NotBlank(message = "La marque est obligatoire")
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    private String model;

    @NotBlank(message = "La couleur est obligatoire")
    private String color;

    @NotBlank(message = "L'immatriculation est obligatoire")
    private String licensePlate;

    @Min(value = 1, message = "Il faut au moins 1 place")
    private int seats;
}

package com.springride.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {
    private Long id;
    private String brand;
    private String model;
    private String color;
    private String licensePlate;
    private int seats;
    private Long ownerId;
}

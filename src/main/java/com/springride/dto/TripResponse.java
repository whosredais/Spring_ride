package com.springride.dto;

import com.springride.model.enums.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripResponse {
    private Long id;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureDateTime;
    private int availableSeats;
    private BigDecimal pricePerSeat;
    private String description;
    private TripStatus status;

    // Driver info
    private Long driverId;
    private String driverName;
    private Double driverRating;

    // Vehicle info
    private String carModel;
    private String carColor;
}

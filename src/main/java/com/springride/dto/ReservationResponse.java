package com.springride.dto;

import com.springride.model.enums.ReservationStatus;
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
public class ReservationResponse {
    private Long id;
    private int seatsRequested;
    private LocalDateTime requestedAt;
    private ReservationStatus status;
    private BigDecimal totalPrice;

    // Trip info
    private Long tripId;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureDateTime;

    // Passenger info (visible to driver)
    private Long passengerId;
    private String passengerName;
}

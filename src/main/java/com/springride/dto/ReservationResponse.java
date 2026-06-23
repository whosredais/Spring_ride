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
    private String passengerEmail;
    private String passengerPhone;

    // We can also include a simplified Passenger DTO if we want to be cleaner,
    // but existing template uses res.passenger.firstname.
    // Let's create a nested DTO or just flat fields?
    // The template uses: res.passenger.firstname, res.passenger.lastname,
    // res.passenger.email, res.passenger.phone
    // So we need a struct that mimics that or change the template.
    // Changing the template to use flat fields is safer for DTO pattern.
    // I will change the template to use flat fields: passengerName, passengerEmail,
    // passengerPhone.

    private String passengerFirstname;
    private String passengerLastname;

    // Driver info (for reviews)
    private Long driverId;
    private String driverName;
}

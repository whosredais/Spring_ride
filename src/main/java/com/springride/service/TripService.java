package com.springride.service;

import com.springride.dto.TripRequest;
import com.springride.dto.TripResponse;
import com.springride.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TripService {
    TripResponse createTrip(TripRequest request, User driver);

    TripResponse getTripById(Long id);

    List<TripResponse> searchTrips(String departure, String arrival, LocalDateTime date, BigDecimal minPrice,
            BigDecimal maxPrice);

    List<TripResponse> getDriverTrips(Long driverId);

    void cancelTrip(Long tripId, Long driverId);
}

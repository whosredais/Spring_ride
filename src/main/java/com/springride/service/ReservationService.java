package com.springride.service;

import com.springride.dto.ReservationRequest;
import com.springride.dto.ReservationResponse;
import com.springride.model.User;
import com.springride.model.enums.ReservationStatus;

import java.util.List;

public interface ReservationService {
    ReservationResponse requestReservation(ReservationRequest request, User passenger);

    ReservationResponse updateStatus(Long reservationId, ReservationStatus status, User driver);

    List<ReservationResponse> getPassengerReservations(Long passengerId);

    List<ReservationResponse> getDriverReservations(Long driverId); // Réservations reçues pour ses trajets
}

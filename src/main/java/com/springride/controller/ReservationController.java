package com.springride.controller;

import com.springride.dto.ReservationRequest;
import com.springride.dto.ReservationResponse;
import com.springride.model.User;
import com.springride.model.enums.ReservationStatus;
import com.springride.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> requestReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal User currentUser) {
        return new ResponseEntity<>(reservationService.requestReservation(request, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(reservationService.updateStatus(id, status, currentUser));
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<List<ReservationResponse>> getMyReservationsAsPassenger(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(reservationService.getPassengerReservations(currentUser.getId()));
    }

    @GetMapping("/received")
    public ResponseEntity<List<ReservationResponse>> getReceivedReservations(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(reservationService.getDriverReservations(currentUser.getId()));
    }
}

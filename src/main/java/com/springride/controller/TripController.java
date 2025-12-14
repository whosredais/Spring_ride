package com.springride.controller;

import com.springride.dto.MessageResponse;
import com.springride.dto.TripRequest;
import com.springride.dto.TripResponse;
import com.springride.model.User;
import com.springride.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody TripRequest request,
            @AuthenticationPrincipal User currentUser) {
        return new ResponseEntity<>(tripService.createTrip(request, currentUser), HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> searchTrips(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String arrival,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(tripService.searchTrips(departure, arrival, date, minPrice, maxPrice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @GetMapping("/my-trips")
    public ResponseEntity<List<TripResponse>> getMyTrips(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(tripService.getDriverTrips(currentUser.getId()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<MessageResponse> cancelTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        tripService.cancelTrip(id, currentUser.getId());
        return ResponseEntity.ok(new MessageResponse("Trajet annulé avec succès"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(tripService.updateTrip(id, request, currentUser.getId()));
    }
}

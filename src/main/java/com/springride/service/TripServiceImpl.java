package com.springride.service;

import com.springride.dto.TripRequest;
import com.springride.dto.TripResponse;
import com.springride.exception.BadRequestException;
import com.springride.exception.ResourceNotFoundException;
import com.springride.model.Trip;
import com.springride.model.User;
import com.springride.model.Vehicle;
import com.springride.model.enums.TripStatus;
import com.springride.repository.TripRepository;
import com.springride.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public TripResponse createTrip(TripRequest request, User driver) {
        if (request.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La date du trajet doit être dans le futur");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        if (!vehicle.getOwner().getId().equals(driver.getId())) {
            throw new BadRequestException("Vous ne pouvez pas utiliser un véhicule qui ne vous appartient pas");
        }

        Trip trip = Trip.builder()
                .departureCity(request.getDepartureCity())
                .arrivalCity(request.getArrivalCity())
                .departureDateTime(request.getDepartureDateTime())
                .estimatedDuration(request.getEstimatedDuration())
                .availableSeats(request.getAvailableSeats())
                .pricePerSeat(request.getPricePerSeat())
                .description(request.getDescription())
                .status(TripStatus.PLANIFIE)
                .driver(driver)
                .vehicle(vehicle)
                .build();

        return mapToResponse(tripRepository.save(trip));
    }

    @Override
    public TripResponse getTripById(Long id) {
        return mapToResponse(tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet non trouvé")));
    }

    @Override
    public List<TripResponse> searchTrips(String departure, String arrival, LocalDateTime date, BigDecimal minPrice,
            BigDecimal maxPrice) {
        return tripRepository.searchTrips(departure, arrival, date, minPrice, maxPrice, LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripResponse> getDriverTrips(Long driverId) {
        return tripRepository.findByDriverId(driverId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelTrip(Long tripId, Long driverId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet non trouvé"));

        if (!trip.getDriver().getId().equals(driverId)) {
            throw new BadRequestException("Vous n'êtes pas le conducteur de ce trajet");
        }

        // Annuler toutes les réservations associées
        if (trip.getReservations() != null && !trip.getReservations().isEmpty()) {
            trip.getReservations().forEach(reservation -> {
                reservation.setStatus(com.springride.model.enums.ReservationStatus.ANNULEE);
            });
            // Les réservations seront sauvegardées automatiquement via cascade
        }

        trip.setStatus(TripStatus.ANNULE);
        tripRepository.save(trip);
    }

    @Override
    public TripResponse updateTrip(Long tripId, TripRequest request, Long driverId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet non trouvé"));

        if (!trip.getDriver().getId().equals(driverId)) {
            throw new BadRequestException("Vous n'êtes pas le conducteur de ce trajet");
        }

        // Vérifier si des réservations existent (sauf annulées)
        boolean hasActiveReservations = trip.getReservations() != null && trip.getReservations().stream()
                .anyMatch(r -> r.getStatus() != com.springride.model.enums.ReservationStatus.ANNULEE
                        && r.getStatus() != com.springride.model.enums.ReservationStatus.REFUSEE);

        if (hasActiveReservations) {
            throw new BadRequestException("Impossible de modifier le trajet car des réservations existent");
        }

        if (request.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La date du trajet doit être dans le futur");
        }

        trip.setDepartureCity(request.getDepartureCity());
        trip.setArrivalCity(request.getArrivalCity());
        trip.setDepartureDateTime(request.getDepartureDateTime());
        trip.setEstimatedDuration(request.getEstimatedDuration());
        trip.setAvailableSeats(request.getAvailableSeats());
        trip.setPricePerSeat(request.getPricePerSeat());
        trip.setDescription(request.getDescription());

        // Update vehicle if changed
        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));
            if (!vehicle.getOwner().getId().equals(driverId)) {
                throw new BadRequestException("Ce véhicule ne vous appartient pas");
            }
            trip.setVehicle(vehicle);
        }

        return mapToResponse(tripRepository.save(trip));
    }

    private TripResponse mapToResponse(Trip trip) {
        System.out.println("DEBUG: Mapping Trip ID=" + trip.getId() + ", Driver=" + trip.getDriver().getEmail()
                + ", Name=" + trip.getDriver().getFirstname());
        return TripResponse.builder()
                .id(trip.getId())
                .departureCity(trip.getDepartureCity())
                .arrivalCity(trip.getArrivalCity())
                .departureDateTime(trip.getDepartureDateTime())
                .estimatedDuration(trip.getEstimatedDuration())
                .availableSeats(trip.getAvailableSeats())
                .pricePerSeat(trip.getPricePerSeat())
                .description(trip.getDescription())
                .status(trip.getStatus())
                .driverId(trip.getDriver().getId())
                .driverName(trip.getDriver().getFirstname() + " " + trip.getDriver().getLastname())
                .driverRating(trip.getDriver().getAverageRating())
                .carModel(trip.getVehicle().getBrand() + " " + trip.getVehicle().getModel())
                .carColor(trip.getVehicle().getColor())
                .vehicleId(trip.getVehicle().getId())
                .driverTripsCount(tripRepository.countTripsWithReservations(trip.getDriver().getId()))
                .reservations(trip.getReservations() != null ? trip.getReservations().stream()
                        .map(res -> com.springride.dto.ReservationResponse.builder()
                                .id(res.getId())
                                .seatsRequested(res.getSeatsRequested())
                                .requestedAt(res.getRequestedAt())
                                .status(res.getStatus())
                                .passengerId(res.getPassenger().getId())
                                .passengerName(
                                        res.getPassenger().getFirstname() + " " + res.getPassenger().getLastname())
                                .passengerEmail(res.getPassenger().getEmail())
                                .passengerPhone(res.getPassenger().getPhone())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }
}

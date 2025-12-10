package com.springride.service;

import com.springride.dto.ReservationRequest;
import com.springride.dto.ReservationResponse;
import com.springride.exception.BadRequestException;
import com.springride.exception.ResourceNotFoundException;
import com.springride.model.Reservation;
import com.springride.model.Trip;
import com.springride.model.User;
import com.springride.model.enums.ReservationStatus;
import com.springride.repository.ReservationRepository;
import com.springride.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;

    @Override
    @Transactional
    public ReservationResponse requestReservation(ReservationRequest request, User passenger) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trajet non trouvé"));

        if (trip.getDriver().getId().equals(passenger.getId())) {
            throw new BadRequestException("Vous ne pouvez pas réserver votre propre trajet");
        }

        if (trip.getAvailableSeats() < request.getSeatsRequested()) {
            throw new BadRequestException("Pas assez de places disponibles");
        }

        Reservation reservation = Reservation.builder()
                .seatsRequested(request.getSeatsRequested())
                .requestedAt(LocalDateTime.now())
                .status(ReservationStatus.DEMANDEE)
                .passenger(passenger)
                .trip(trip)
                .build();

        return mapToResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse updateStatus(Long reservationId, ReservationStatus newStatus, User currentUser) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        Trip trip = reservation.getTrip();

        // Logique d'autorisation et de transition d'état
        if (newStatus == ReservationStatus.CONFIRMEE || newStatus == ReservationStatus.REFUSEE) {
            // Seul le conducteur peut confirmer ou refuser
            if (!trip.getDriver().getId().equals(currentUser.getId())) {
                throw new BadRequestException("Seul le conducteur peut gérer cette réservation");
            }

            if (reservation.getStatus() != ReservationStatus.DEMANDEE) {
                throw new BadRequestException("Cette réservation a déjà été traitée");
            }

            if (newStatus == ReservationStatus.CONFIRMEE) {
                if (trip.getAvailableSeats() < reservation.getSeatsRequested()) {
                    throw new BadRequestException("Plus de places disponibles pour confirmer cette demande");
                }
                // Mise à jour des places
                trip.setAvailableSeats(trip.getAvailableSeats() - reservation.getSeatsRequested());
                tripRepository.save(trip);
            }
        } else if (newStatus == ReservationStatus.ANNULEE) {
            // Le passager peut annuler sa demande
            if (!reservation.getPassenger().getId().equals(currentUser.getId())) {
                throw new BadRequestException("Seul le passager peut annuler cette réservation");
            }

            // Si la réservation était déjà confirmée, on doit rendre les places
            if (reservation.getStatus() == ReservationStatus.CONFIRMEE) {
                trip.setAvailableSeats(trip.getAvailableSeats() + reservation.getSeatsRequested());
                tripRepository.save(trip);
            }
        } else {
            throw new BadRequestException("Transition de statut non gérée");
        }

        reservation.setStatus(newStatus);
        return mapToResponse(reservationRepository.save(reservation));
    }

    @Override
    public List<ReservationResponse> getPassengerReservations(Long passengerId) {
        return reservationRepository.findByPassengerId(passengerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getDriverReservations(Long driverId) {
        return reservationRepository.findByTripDriverId(driverId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReservationResponse mapToResponse(Reservation r) {
        BigDecimal totalPrice = r.getTrip().getPricePerSeat().multiply(BigDecimal.valueOf(r.getSeatsRequested()));

        return ReservationResponse.builder()
                .id(r.getId())
                .seatsRequested(r.getSeatsRequested())
                .requestedAt(r.getRequestedAt())
                .status(r.getStatus())
                .totalPrice(totalPrice)
                .tripId(r.getTrip().getId())
                .departureCity(r.getTrip().getDepartureCity())
                .arrivalCity(r.getTrip().getArrivalCity())
                .departureDateTime(r.getTrip().getDepartureDateTime())
                .passengerId(r.getPassenger().getId())
                .passengerName(r.getPassenger().getFirstname() + " " + r.getPassenger().getLastname())
                .build();
    }
}

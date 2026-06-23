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
import com.springride.model.enums.TripStatus;
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

        if (reservationRepository.existsByTripIdAndPassengerId(trip.getId(), passenger.getId())) {
            throw new BadRequestException("Vous avez déjà réservé ce trajet");
        }

        if (trip.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Ce trajet est déjà passé");
        }

        if (trip.getStatus() == TripStatus.ANNULE
                || trip.getStatus() == TripStatus.EXPIRE
                || trip.getStatus() == TripStatus.TERMINEE
                || trip.getStatus() == TripStatus.COMPLET) {
            throw new BadRequestException("Ce trajet n'est plus disponible à la réservation");
        }

        if (trip.getAvailableSeats() < request.getSeatsRequested()) {
            throw new BadRequestException("Pas assez de places disponibles");
        }

        // ACCEPTATION AUTOMATIQUE : Déduire les places immédiatement
        trip.setAvailableSeats(trip.getAvailableSeats() - request.getSeatsRequested());

        // Si plus de places, marquer le trajet comme COMPLET
        if (trip.getAvailableSeats() == 0) {
            trip.setStatus(com.springride.model.enums.TripStatus.COMPLET);
        }

        tripRepository.save(trip);

        // Créer la réservation avec statut CONFIRMEE (acceptation automatique)
        Reservation reservation = Reservation.builder()
                .seatsRequested(request.getSeatsRequested())
                .requestedAt(LocalDateTime.now())
                .status(ReservationStatus.CONFIRMEE) // Statut initial: CONFIRMEE (acceptation automatique)
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

        // ACCEPTATION AUTOMATIQUE : Seule l'annulation est gérée
        if (newStatus == ReservationStatus.ANNULEE) {
            // Le passager ou le conducteur peut annuler
            boolean isPassenger = reservation.getPassenger().getId().equals(currentUser.getId());
            boolean isDriver = trip.getDriver().getId().equals(currentUser.getId());

            if (!isPassenger && !isDriver) {
                throw new BadRequestException("Vous n'êtes pas autorisé à annuler cette réservation");
            }

            // Vérifier que la réservation n'est pas déjà annulée
            if (reservation.getStatus() == ReservationStatus.ANNULEE) {
                throw new BadRequestException("Cette réservation est déjà annulée");
            }

            // Remettre les places (car elles ont été déduites lors de la création)
            if (reservation.getStatus() == ReservationStatus.CONFIRMEE) {
                trip.setAvailableSeats(trip.getAvailableSeats() + reservation.getSeatsRequested());

                // Si le trajet était COMPLET, il redevient PLANIFIE car des places se libèrent
                if (trip.getStatus() == com.springride.model.enums.TripStatus.COMPLET) {
                    trip.setStatus(com.springride.model.enums.TripStatus.PLANIFIE);
                }
                tripRepository.save(trip);
            }

            reservation.setStatus(newStatus);
            return mapToResponse(reservationRepository.save(reservation));
        } else {
            throw new BadRequestException(
                    "Seule l'annulation est autorisée. Les réservations sont confirmées automatiquement.");
        }
    }

    @Override
    public List<ReservationResponse> getPassengerReservations(Long passengerId) {
        return reservationRepository.findByPassengerId(passengerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getUpcomingReservations(Long passengerId) {
        return reservationRepository.findUpcomingReservations(passengerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getReservationsToRate(Long passengerId) {
        return reservationRepository.findReservationsToRate(passengerId).stream()
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
                .driverId(r.getTrip().getDriver().getId())
                .driverName(r.getTrip().getDriver().getFirstname() + " " + r.getTrip().getDriver().getLastname())
                .build();
    }
}

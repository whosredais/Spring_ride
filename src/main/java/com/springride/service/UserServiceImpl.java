package com.springride.service;

import com.springride.dto.*;
import com.springride.exception.ResourceNotFoundException;
import com.springride.model.User;
import com.springride.repository.ReservationRepository;
import com.springride.repository.TripRepository;
import com.springride.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final TripRepository tripRepository;
        private final ReservationRepository reservationRepository;

        @Override
        public UserResponse getUserProfile(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

                return UserResponse.builder()
                                .id(user.getId())
                                .firstname(user.getFirstname())
                                .lastname(user.getLastname())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .averageRating(user.getAverageRating() != null ? user.getAverageRating() : 0.0)
                                .reviewCount(user.getReviewCount() != null ? user.getReviewCount() : 0)
                                .build();
        }

        @Override
        public DashboardResponse getUserDashboard(Long userId) {
                UserResponse profile = getUserProfile(userId);

                // Récupérer les trajets proposés (Conducteur)
                List<TripResponse> myTrips = tripRepository.findByDriverId(userId).stream()
                                .map(trip -> TripResponse.builder()
                                                .id(trip.getId())
                                                .departureCity(trip.getDepartureCity())
                                                .arrivalCity(trip.getArrivalCity())
                                                .departureDateTime(trip.getDepartureDateTime())
                                                .status(trip.getStatus())
                                                .build())
                                .collect(Collectors.toList());

                // Récupérer les réservations faites (Passager)
                List<ReservationResponse> myReservations = reservationRepository.findByPassengerId(userId).stream()
                                .map(res -> ReservationResponse.builder()
                                                .id(res.getId())
                                                .status(res.getStatus())
                                                .departureCity(res.getTrip().getDepartureCity())
                                                .arrivalCity(res.getTrip().getArrivalCity())
                                                .departureDateTime(res.getTrip().getDepartureDateTime())
                                                .build())
                                .collect(Collectors.toList());

                return DashboardResponse.builder()
                                .userProfile(profile)
                                .totalTripsAsDriver(myTrips.size())
                                .totalTripsAsPassenger(myReservations.size())
                                .averageRating(profile.getAverageRating())
                                .recentTripsPublished(myTrips)
                                .recentReservationsRequest(myReservations)
                                .build();
        }
}

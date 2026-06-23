package com.springride.service;

import com.springride.dto.*;
import com.springride.exception.ResourceNotFoundException;
import com.springride.model.User;
import com.springride.repository.ReservationRepository;
import com.springride.repository.TripRepository;
import com.springride.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
                                .profilePicture(user.getProfilePicture())
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
                                .estimatedRevenue(reservationRepository.calculateDriverRevenue(userId))
                                .recentTripsPublished(myTrips)
                                .recentReservationsRequest(myReservations)
                                .strikes(userRepository.findById(userId)
                                                .map(u -> u.getStrikes() != null ? u.getStrikes() : 0)
                                                .orElse(0))
                                .warningMessage(userRepository.findById(userId).map(User::getWarningMessage)
                                                .orElse(null))
                                .build();
        }

        @Override
        public void updateProfile(Long userId, ProfileRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

                user.setFirstname(request.getFirstname());
                user.setLastname(request.getLastname());
                user.setPhone(request.getPhone());

                if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
                        try {
                                String fileName = System.currentTimeMillis() + "_"
                                                + request.getProfilePicture().getOriginalFilename();
                                Path uploadPath = Paths.get("uploads");
                                if (!Files.exists(uploadPath)) {
                                        Files.createDirectories(uploadPath);
                                }
                                Files.copy(request.getProfilePicture().getInputStream(),
                                                uploadPath.resolve(fileName),
                                                StandardCopyOption.REPLACE_EXISTING);
                                user.setProfilePicture(fileName);
                        } catch (IOException e) {
                                throw new RuntimeException("Erreur lors de l'upload de l'image", e);
                        }
                }

                userRepository.save(user);
        }

        @Override
        public User getUserByEmail(String email) {
                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Utilisateur non trouvé avec l'email : " + email));
        }

        @Override
        public void warnUser(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

                // Increment strikes
                int currentStrikes = user.getStrikes() == null ? 0 : user.getStrikes();
                int newStrikes = currentStrikes + 1;
                user.setStrikes(newStrikes);

                // Generate automatic message
                if (newStrikes == 1) {
                        user.setWarningMessage(
                                        "Attention : Vous avez reçu un premier avertissement. Tout nouvel avertissement entraînera la suspension définitive de votre compte. Ceci est votre dernière chance.");
                } else if (newStrikes >= 2) {
                        user.setWarningMessage("Votre compte a été suspendu suite à de multiples avertissements.");
                        user.setActive(false);
                }

                userRepository.save(user);
        }
}

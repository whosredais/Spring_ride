package com.springride.service;

import com.springride.model.Report;
import com.springride.model.Trip;
import com.springride.model.User;
// import com.springride.model.Report.ReportStatus;
import com.springride.repository.ReportRepository;
import com.springride.repository.TripRepository;
import com.springride.repository.UserRepository;
import com.springride.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final TripRepository tripRepository;
    private final com.springride.repository.ReservationRepository reservationRepository;
    private final com.springride.repository.VehicleRepository vehicleRepository;
    private final com.springride.repository.ReviewRepository reviewRepository;

    // User Management
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);

        // 1. Supprimer les Rapports liés (Reporter ou Reported)
        reportRepository.deleteByReporterId(id);
        reportRepository.deleteByReportedUserId(id);

        // 2. Supprimer les Avis liés (Reviewer ou Reviewed)
        reviewRepository.deleteByReviewerId(id);
        reviewRepository.deleteByReviewedId(id);

        // 3. Supprimer les Véhicules
        vehicleRepository.deleteByOwnerId(id);

        // 4. Supprimer l'historique de réservations (Passager)
        reservationRepository.deleteByPassengerId(id);

        // 5. Supprimer les Trajets (Conducteur)
        java.util.List<Trip> trips = tripRepository.findByDriverId(id);
        for (Trip trip : trips) {
            deleteTrip(trip.getId()); // Re-use deleteTrip logic (which is simple deleteById)
        }

        // 6. Finally delete the User
        userRepository.delete(user);
    }

    // Report Management
    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public List<Report> getReportsByStatus(Report.ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    @Override
    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    @Override
    public void updateReportStatus(Long id, Report.ReportStatus status) {
        Report report = getReportById(id);
        report.setStatus(status);
        reportRepository.save(report);

        // Automating warning message if report is accepted (RESOLVED)
        if (status == Report.ReportStatus.RESOLVED) {
            User reportedUser = report.getReportedUser();
            int currentStrikes = reportedUser.getStrikes() == null ? 0 : reportedUser.getStrikes();
            int newStrikes = currentStrikes + 1;
            reportedUser.setStrikes(newStrikes);

            if (newStrikes == 1) {
                reportedUser.setWarningMessage(
                        "Attention : Vous avez reçu un premier avertissement. Tout nouvel avertissement entraînera la suspension définitive de votre compte.");
            } else if (newStrikes >= 2) {
                reportedUser.setWarningMessage("Votre compte a été suspendu suite à de multiples avertissements.");
                reportedUser.setActive(false);
            }
            userRepository.save(reportedUser);
        }
    }

    // Trip Management
    @Override
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Override
    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }

    @Override
    public List<com.springride.model.Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public void sendWarning(Long userId, String message) {
        User user = getUserById(userId);
        user.setWarningMessage(message);
        userRepository.save(user);
    }
}

package com.springride.service;

import com.springride.model.Report;
import com.springride.model.Trip;
import com.springride.model.User;
// import com.springride.model.Report.ReportStatus;

import java.util.List;

public interface AdminService {

    // User Management
    List<User> getAllUsers();

    User getUserById(Long id);

    void toggleUserStatus(Long id);

    void deleteUser(Long id);

    // Report Management
    List<Report> getAllReports();

    List<Report> getReportsByStatus(Report.ReportStatus status);

    Report getReportById(Long id);

    void updateReportStatus(Long id, Report.ReportStatus status);

    // Trip Management
    List<Trip> getAllTrips();

    void deleteTrip(Long id);

    // Reservation Management (for stats)
    List<com.springride.model.Reservation> getAllReservations();
}

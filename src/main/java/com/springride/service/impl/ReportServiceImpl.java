package com.springride.service.impl;

import com.springride.model.Report;
import com.springride.model.User;
import com.springride.repository.ReportRepository;
import com.springride.repository.UserRepository;
import com.springride.repository.TripRepository;
import com.springride.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    @Override
    public void createReport(Long reportedUserId, String reason, User reporter) {
        createReport(reportedUserId, null, reason, reporter);
    }

    @Override
    public void createReport(Long reportedUserId, Long tripId, String reason, User reporter) {
        if (reporter.getId().equals(reportedUserId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous signaler vous-même.");
        }

        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur à signaler non trouvé"));

        com.springride.model.Trip trip = null;
        if (tripId != null) {
            trip = tripRepository.findById(tripId).orElse(null);
        }

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .trip(trip)
                .reason(reason)
                .status(Report.ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        reportRepository.save(report);
    }

    @Override
    public java.util.List<Report> getReportsByUser(User user) {
        return reportRepository.findByReporterOrderByCreatedAtDesc(user);
    }

    @Override
    public java.util.List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public void resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Signalement non trouvé"));
        report.setStatus(Report.ReportStatus.RESOLVED);
        reportRepository.save(report);
    }

    @Override
    public void dismissReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Signalement non trouvé"));
        report.setStatus(Report.ReportStatus.DISMISSED);
        reportRepository.save(report);
    }
}

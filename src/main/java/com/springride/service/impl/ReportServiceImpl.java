package com.springride.service.impl;

import com.springride.model.Report;
import com.springride.model.User;
import com.springride.repository.ReportRepository;
import com.springride.repository.UserRepository;
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

    @Override
    public void createReport(Long reportedUserId, String reason, User reporter) {
        if (reporter.getId().equals(reportedUserId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous signaler vous-même.");
        }

        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur à signaler non trouvé"));

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(reason)
                .status(Report.ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        reportRepository.save(report);
    }
}

package com.springride.service;

import com.springride.model.Report;
import com.springride.model.User;
import java.util.List;

public interface ReportService {
    void createReport(Long reportedUserId, String reason, User reporter);

    void createReport(Long reportedUserId, Long tripId, String reason, User reporter);

    List<Report> getReportsByUser(User user);

    List<Report> getAllReports();

    void resolveReport(Long reportId);

    void dismissReport(Long reportId);
}

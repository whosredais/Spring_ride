package com.springride.service;

import com.springride.model.Report;
import com.springride.model.User;

public interface ReportService {
    void createReport(Long reportedUserId, String reason, User reporter);
}

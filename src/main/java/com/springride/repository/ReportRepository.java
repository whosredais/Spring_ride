package com.springride.repository;

import com.springride.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatus(Report.ReportStatus status);

    void deleteByReporterId(Long reporterId);

    void deleteByReportedUserId(Long reportedUserId);

    List<Report> findByReporterOrderByCreatedAtDesc(com.springride.model.User reporter);

    List<Report> findAllByOrderByCreatedAtDesc();
}

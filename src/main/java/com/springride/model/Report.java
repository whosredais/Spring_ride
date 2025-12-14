package com.springride.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = true) // Can be null if reporting a trip generally or backend
                                                            // logic handles it
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    public enum ReportStatus {
        PENDING,
        RESOLVED,
        DISMISSED
    }
}

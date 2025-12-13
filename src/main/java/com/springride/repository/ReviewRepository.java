package com.springride.repository;

import com.springride.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedId(Long reviewedId);

    // Vérifier si un review existe déjà pour ce trajet par cet utilisateur
    boolean existsByTripIdAndReviewerId(Long tripId, Long reviewerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewed.id = :userId")
    Double calculateAverageRating(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewed.id = :userId")
    Integer countReviewsForUser(@Param("userId") Long userId);

    void deleteByReviewerId(Long reviewerId);

    void deleteByReviewedId(Long reviewedId);
}
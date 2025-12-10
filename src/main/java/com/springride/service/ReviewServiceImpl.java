package com.springride.service;

import com.springride.dto.ReviewRequest;
import com.springride.dto.ReviewResponse;
import com.springride.exception.BadRequestException;
import com.springride.exception.ResourceNotFoundException;
import com.springride.model.Review;
import com.springride.model.Trip;
import com.springride.model.User;
import com.springride.repository.ReviewRepository;
import com.springride.repository.TripRepository;
import com.springride.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest request, User reviewer) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trajet non trouvé"));

        if (reviewer.getId().equals(request.getReviewedUserId())) {
            throw new BadRequestException("Vous ne pouvez pas vous noter vous-même");
        }

        User reviewedUser = userRepository.findById(request.getReviewedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .reviewer(reviewer)
                .reviewed(reviewedUser)
                .trip(trip)
                .build();

        Review savedReview = reviewRepository.save(review);

        // Recalcul de la note moyenne
        updateAverageRating(reviewedUser);

        return mapToResponse(savedReview);
    }

    @Override
    public List<ReviewResponse> getReviewsForUser(Long userId) {
        return reviewRepository.findByReviewedId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void updateAverageRating(User user) {
        List<Review> reviews = reviewRepository.findByReviewedId(user.getId());
        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        user.setAverageRating(Math.round(average * 10.0) / 10.0); // Arrondi à 1 décimale
        user.setReviewCount(reviews.size());
        userRepository.save(user);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewerName(review.getReviewer().getFirstname() + " " + review.getReviewer().getLastname())
                .tripId(review.getTrip().getId())
                .build();
    }
}

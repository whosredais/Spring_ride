package com.springride.service;

import com.springride.dto.ReviewRequest;
import com.springride.dto.ReviewResponse;
import com.springride.model.User;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(ReviewRequest request, User reviewer);

    List<ReviewResponse> getReviewsForUser(Long userId);
}

// 5. ReviewRepository.java
package com.springride.repository;

import com.springride.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
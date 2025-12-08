// 3. TripRepository.java
package com.springride.repository;

import com.springride.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    // Recherche simple par villes (on améliorera plus tard)
    List<Trip> findByDepartureCityContainingIgnoreCaseAndArrivalCityContainingIgnoreCase(
            String departure, String arrival);

    // Trajets futurs
    List<Trip> findByDepartureDateTimeAfter(LocalDateTime now);
}
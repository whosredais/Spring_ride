// 3. TripRepository.java
package com.springride.repository;

import com.springride.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t WHERE " +
            "(:departure IS NULL OR t.departureCity LIKE %:departure%) AND " +
            "(:arrival IS NULL OR t.arrivalCity LIKE %:arrival%) AND " +
            "(:date IS NULL OR t.departureDateTime >= :date) AND " +
            "(:minPrice IS NULL OR t.pricePerSeat >= :minPrice) AND " +
            "(:maxPrice IS NULL OR t.pricePerSeat <= :maxPrice) AND " +
            "t.status = 'PLANIFIE' AND t.availableSeats > 0")
    List<Trip> searchTrips(
            @Param("departure") String departure,
            @Param("arrival") String arrival,
            @Param("date") LocalDateTime date,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    List<Trip> findByDriverId(Long driverId);

    // Recherche simple par villes (on améliorera plus tard)
    List<Trip> findByDepartureCityContainingIgnoreCaseAndArrivalCityContainingIgnoreCase(
            String departure, String arrival);

    // Trajets futurs
    List<Trip> findByDepartureDateTimeAfter(LocalDateTime now);
}
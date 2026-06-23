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
                        "(:departure IS NULL OR t.departureCity = :departure) AND " +
                        "(:arrival IS NULL OR t.arrivalCity = :arrival) AND " +
                        "t.departureDateTime > :now AND " +
                        "t.status = 'PLANIFIE' AND " +
                        "t.availableSeats > 0 AND " +
                        "(:date IS NULL OR CAST(t.departureDateTime AS LocalDate) = CAST(:date AS LocalDate)) AND " +
                        "(:minPrice IS NULL OR t.pricePerSeat >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR t.pricePerSeat <= :maxPrice) " +
                        "ORDER BY t.departureDateTime ASC")
        List<Trip> searchTrips(
                        @Param("departure") String departure,
                        @Param("arrival") String arrival,
                        @Param("date") LocalDateTime date,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("now") LocalDateTime now);

        @Query("SELECT t FROM Trip t WHERE t.departureDateTime < :now AND t.status IN ('PLANIFIE', 'COMPLET')")
        List<Trip> findExpiredTrips(@Param("now") LocalDateTime now);

        List<Trip> findByDriverId(Long driverId);

        // Recherche simple par villes (on améliorera plus tard)
        List<Trip> findByDepartureCityContainingIgnoreCaseAndArrivalCityContainingIgnoreCase(
                        String departure, String arrival);

        // Trajets futurs
        List<Trip> findByDepartureDateTimeAfter(LocalDateTime now);

        @Query("SELECT COUNT(DISTINCT t) FROM Trip t JOIN t.reservations r WHERE t.driver.id = :driverId")
        Long countTripsWithReservations(@Param("driverId") Long driverId);
}
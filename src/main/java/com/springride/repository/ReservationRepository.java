// 4. ReservationRepository.java
package com.springride.repository;

import com.springride.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByPassengerId(Long passengerId);

    @Query("SELECT COALESCE(SUM(r.seatsRequested * t.pricePerSeat), 0) FROM Reservation r JOIN r.trip t WHERE t.driver.id = :driverId AND (r.status = 'CONFIRMEE' OR r.status = 'TERMINEE')")
    BigDecimal calculateDriverRevenue(@Param("driverId") Long driverId);

    List<Reservation> findByTripId(Long tripId);

    List<Reservation> findByTripDriverId(Long driverId);

    boolean existsByTripIdAndPassengerId(Long tripId, Long passengerId);

    @Query("SELECT r FROM Reservation r WHERE r.passenger.id = :passengerId AND r.trip.departureDateTime > CURRENT_TIMESTAMP AND r.status != 'ANNULEE' AND r.status != 'REFUSEE'")
    List<Reservation> findUpcomingReservations(@Param("passengerId") Long passengerId);

    // Réservations dont l'heure de départ est passée, confirmées, sans avis laissé
    @Query("SELECT r FROM Reservation r WHERE r.passenger.id = :passengerId AND r.trip.departureDateTime <= CURRENT_TIMESTAMP AND r.status = 'CONFIRMEE' AND NOT EXISTS (SELECT rv FROM Review rv WHERE rv.trip.id = r.trip.id AND rv.reviewer.id = :passengerId)")
    List<Reservation> findReservationsToRate(@Param("passengerId") Long passengerId);

    // Historique complet (passés ou annulés)
    @Query("SELECT r FROM Reservation r WHERE r.passenger.id = :passengerId AND (r.trip.departureDateTime <= CURRENT_TIMESTAMP OR r.status = 'ANNULEE' OR r.status = 'REFUSEE') ORDER BY r.trip.departureDateTime DESC")
    List<Reservation> findPastReservations(@Param("passengerId") Long passengerId);
}
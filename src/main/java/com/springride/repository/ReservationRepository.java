// 4. ReservationRepository.java
package com.springride.repository;

import com.springride.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByPassengerId(Long passengerId);
    List<Reservation> findByTripId(Long tripId);
}
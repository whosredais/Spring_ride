// 2. VehicleRepository.java
package com.springride.repository;

import com.springride.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByOwnerId(Long ownerId);

    void deleteByOwnerId(Long ownerId);
}
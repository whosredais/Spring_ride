// 2. VehicleRepository.java
package com.springride.repository;

import com.springride.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    java.util.Optional<Vehicle> findByLicensePlate(String licensePlate);

    java.util.List<Vehicle> findByOwnerId(Long ownerId);

    void deleteByOwnerId(Long ownerId);
}
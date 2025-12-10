package com.springride.service;

import com.springride.dto.VehicleRequest;
import com.springride.dto.VehicleResponse;
import com.springride.model.User;

import java.util.List;

public interface VehicleService {
    VehicleResponse addVehicle(VehicleRequest request, User owner);

    VehicleResponse getVehicleById(Long id);

    List<VehicleResponse> getVehiclesByOwner(Long ownerId);

    void deleteVehicle(Long id, Long userId);
}

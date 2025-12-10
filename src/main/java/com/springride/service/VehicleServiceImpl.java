package com.springride.service;

import com.springride.dto.VehicleRequest;
import com.springride.dto.VehicleResponse;
import com.springride.exception.ResourceNotFoundException;
import com.springride.exception.BadRequestException;
import com.springride.model.User;
import com.springride.model.Vehicle;
import com.springride.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public VehicleResponse addVehicle(VehicleRequest request, User owner) {
        if (vehicleRepository.findByLicensePlate(request.getLicensePlate()).isPresent()) {
            throw new BadRequestException("Un véhicule avec cette immatriculation existe déjà");
        }

        Vehicle vehicle = Vehicle.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .color(request.getColor())
                .licensePlate(request.getLicensePlate())
                .seats(request.getSeats())
                .owner(owner)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    @Override
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule non trouvé avec l'id :: " + id));
        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> getVehiclesByOwner(Long ownerId) {
        return vehicleRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id, Long userId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule non trouvé avec l'id :: " + id));

        if (!vehicle.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Vous n'êtes pas autorisé à supprimer ce véhicule");
        }

        vehicleRepository.delete(vehicle);
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .color(vehicle.getColor())
                .licensePlate(vehicle.getLicensePlate())
                .seats(vehicle.getSeats())
                .ownerId(vehicle.getOwner().getId())
                .build();
    }
}

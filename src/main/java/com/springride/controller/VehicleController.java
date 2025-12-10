package com.springride.controller;

import com.springride.dto.MessageResponse;
import com.springride.dto.VehicleRequest;
import com.springride.dto.VehicleResponse;
import com.springride.model.User;
import com.springride.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(
            @Valid @RequestBody VehicleRequest request,
            @AuthenticationPrincipal User currentUser) {
        return new ResponseEntity<>(vehicleService.addVehicle(request, currentUser), HttpStatus.CREATED);
    }

    @GetMapping("/my-vehicles")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(vehicleService.getVehiclesByOwner(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteVehicle(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        vehicleService.deleteVehicle(id, currentUser.getId());
        return ResponseEntity.ok(new MessageResponse("Véhicule supprimé avec succès"));
    }
}

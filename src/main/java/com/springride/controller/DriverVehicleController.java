package com.springride.controller;

import com.springride.dto.VehicleRequest;
import com.springride.model.User;
import com.springride.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverVehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/vehicles")
    public String vehicles(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(currentUser.getId()));
        return "driver/vehicles";
    }

    @GetMapping("/vehicles/add")
    public String addVehicleForm(Model model) {
        model.addAttribute("vehicleRequest", new VehicleRequest());
        return "driver/add-vehicle";
    }

    @PostMapping("/vehicles/add")
    public String processAddVehicle(@ModelAttribute VehicleRequest vehicleRequest,
            @AuthenticationPrincipal User currentUser) {
        vehicleService.addVehicle(vehicleRequest, currentUser);
        return "redirect:/driver/vehicles";
    }

    @PostMapping("/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id, currentUser.getId());
            redirectAttributes.addFlashAttribute("success", "Véhicule supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer le véhicule");
        }
        return "redirect:/driver/vehicles";
    }
}

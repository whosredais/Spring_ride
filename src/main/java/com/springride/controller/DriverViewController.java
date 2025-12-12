package com.springride.controller;

import com.springride.dto.TripRequest;
import com.springride.dto.VehicleRequest;
import com.springride.model.User;
import com.springride.service.TripService;
import com.springride.service.UserService;
import com.springride.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverViewController {

    private final UserService userService;
    private final TripService tripService;
    private final VehicleService vehicleService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User currentUser, Model model) {
        var dashboard = userService.getUserDashboard(currentUser.getId());
        model.addAttribute("dashboard", dashboard);
        return "driver/dashboard";
    }

    @GetMapping("/my-trips")
    public String myTrips(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("trips", tripService.getDriverTrips(currentUser.getId()));
        return "driver/my-trips";
    }

    @ModelAttribute("cities")
    public java.util.List<String> cities() {
        return com.springride.util.CityUtils.MOROCCAN_CITIES;
    }

    @GetMapping("/publish")
    public String publishForm(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("tripRequest", new TripRequest());
        model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(currentUser.getId()));
        return "driver/publish";
    }

    @PostMapping("/publish")
    public String processPublish(@jakarta.validation.Valid @ModelAttribute TripRequest tripRequest,
            org.springframework.validation.BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal User currentUser) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(currentUser.getId()));
            return "driver/publish";
        }
        tripService.createTrip(tripRequest, currentUser);
        return "redirect:/driver/my-trips";
    }

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
}

package com.springride.controller;

import com.springride.dto.TripRequest;
import com.springride.model.User;
import com.springride.service.TripService;
import com.springride.service.VehicleService;
import com.springride.util.CityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.List;

@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverTripController {

    private final TripService tripService;
    private final VehicleService vehicleService;

    @GetMapping("/my-trips")
    public String myTrips(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("trips", tripService.getDriverTrips(currentUser.getId()));
        return "driver/my-trips";
    }

    @ModelAttribute("cities")
    public List<String> cities() {
        return CityUtils.MOROCCAN_CITIES;
    }

    @GetMapping("/publish")
    public String publishForm(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("tripRequest", new TripRequest());
        model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(currentUser.getId()));
        return "driver/publish";
    }

    @PostMapping("/publish")
    public String processPublish(@Valid @ModelAttribute TripRequest tripRequest,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal User currentUser) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(currentUser.getId()));
            return "driver/publish";
        }
        tripService.createTrip(tripRequest, currentUser);
        return "redirect:/driver/my-trips";
    }

    @PostMapping("/trips/{id}/cancel")
    public String cancelTrip(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {
        try {
            tripService.cancelTrip(id, currentUser.getId());
            redirectAttributes.addFlashAttribute("success", "Trajet annulé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'annulation du trajet");
        }
        return "redirect:/driver/my-trips";
    }

    @GetMapping("/trips/{id}/edit")
    public String editTripForm(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        var trip = tripService.getTripById(id);

        TripRequest request = new TripRequest();
        request.setDepartureCity(trip.getDepartureCity());
        request.setArrivalCity(trip.getArrivalCity());
        request.setDepartureDateTime(trip.getDepartureDateTime());
        request.setAvailableSeats(trip.getAvailableSeats());
        request.setPricePerSeat(trip.getPricePerSeat());
        request.setVehicleId(trip.getVehicleId());
        request.setEstimatedDuration(trip.getEstimatedDuration());
        request.setDescription(trip.getDescription());

        model.addAttribute("tripRequest", request);
        model.addAttribute("tripId", id);
        model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(currentUser.getId()));
        return "driver/trip-edit";
    }

    @PostMapping("/trips/{id}/edit")
    public String processEditTrip(@PathVariable Long id,
            @Valid @ModelAttribute TripRequest tripRequest,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(currentUser.getId()));
            model.addAttribute("tripId", id);
            return "driver/trip-edit";
        }

        try {
            tripService.updateTrip(id, tripRequest, currentUser.getId());
            redirectAttributes.addFlashAttribute("success", "Trajet modifié avec succès");
            return "redirect:/driver/my-trips";
        } catch (Exception e) {
            model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(currentUser.getId()));
            model.addAttribute("tripId", id);
            model.addAttribute("error", e.getMessage());
            return "driver/trip-edit";
        }
    }
}

package com.springride.controller;

import com.springride.dto.ReservationRequest;
import com.springride.model.User;
import com.springride.service.ReservationService;
import com.springride.service.TripService;
import com.springride.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    private final TripService tripService;
    private final ReservationService reservationService;
    private final UserService userService;

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String arrival,
            Model model) {
        var trips = tripService.searchTrips(departure, arrival, LocalDateTime.now(), null, null);
        model.addAttribute("trips", trips);
        return "user/search";
    }

    @GetMapping("/trips/{id}")
    public String tripDetails(@PathVariable Long id, Model model) {
        model.addAttribute("trip", tripService.getTripById(id));
        model.addAttribute("reservationRequest", new ReservationRequest());
        return "user/trip-details";
    }

    @PostMapping("/trips/{id}/book")
    public String bookTrip(@PathVariable Long id, @ModelAttribute ReservationRequest request,
            @AuthenticationPrincipal User currentUser) {
        request.setTripId(id);
        reservationService.requestReservation(request, currentUser);
        return "redirect:/reservations";
    }

    @GetMapping("/reservations")
    public String myReservations(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("reservations", reservationService.getPassengerReservations(currentUser.getId()));
        return "user/reservations";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("user", userService.getUserProfile(currentUser.getId()));
        return "user/profile";
    }
}

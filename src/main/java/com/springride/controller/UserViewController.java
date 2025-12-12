package com.springride.controller;

import com.springride.dto.ReservationRequest;
import com.springride.model.User;
import com.springride.service.ReservationService;
import com.springride.service.ReviewService;
import com.springride.service.TripService;
import com.springride.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    private final TripService tripService;
    private final ReservationService reservationService;
    private final UserService userService;
    private final ReviewService reviewService;

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String arrival,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            Model model) {

        LocalDateTime searchDate = date != null ? date.atStartOfDay() : null;

        var trips = tripService.searchTrips(departure, arrival, searchDate, minPrice, maxPrice);
        model.addAttribute("trips", trips);
        model.addAttribute("cities", com.springride.util.CityUtils.MOROCCAN_CITIES);
        return "user/search";
    }

    @GetMapping("/trips/{id}")
    public String tripDetails(@PathVariable Long id, @RequestParam(required = false) String error, Model model) {
        model.addAttribute("trip", tripService.getTripById(id));
        model.addAttribute("reservationRequest", new ReservationRequest());
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "user/trip-details";
    }

    @PostMapping("/trips/{id}/book")
    public String bookTrip(@PathVariable Long id, @ModelAttribute ReservationRequest request,
            @AuthenticationPrincipal User currentUser,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            request.setTripId(id);
            reservationService.requestReservation(request, currentUser);
            return "redirect:/reservations";
        } catch (com.springride.exception.BadRequestException e) {
            try {
                String errorMessage = java.net.URLEncoder.encode(e.getMessage(),
                        java.nio.charset.StandardCharsets.UTF_8.toString());
                return "redirect:/trips/" + id + "?error=" + errorMessage;
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/trips/" + id + "?error=Erreur";
            }
        }
    }

    @GetMapping("/reservations")
    public String myReservations(@AuthenticationPrincipal User currentUser, Model model) {
        var allReservations = reservationService.getPassengerReservations(currentUser.getId());
        var upcoming = reservationService.getUpcomingReservations(currentUser.getId());
        var toRate = reservationService.getReservationsToRate(currentUser.getId());

        model.addAttribute("allReservations", allReservations);
        model.addAttribute("upcomingReservations", upcoming);
        model.addAttribute("reservationsToRate", toRate);
        model.addAttribute("newReview", new com.springride.dto.ReviewRequest());

        // Helper boolean for empty state
        boolean hasNoReservations = allReservations.isEmpty();
        model.addAttribute("hasNoReservations", hasNoReservations);

        return "user/reservations";
    }

    @PostMapping("/reviews")
    public String addReview(@ModelAttribute("newReview") com.springride.dto.ReviewRequest request,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {
        try {
            reviewService.addReview(request, currentUser);
            redirectAttributes.addFlashAttribute("success", "Votre avis a été ajouté avec succès !");
        } catch (com.springride.exception.BadRequestException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de l'ajout de votre avis");
        }
        return "redirect:/reservations";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("user", userService.getUserProfile(currentUser.getId()));
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(@AuthenticationPrincipal User currentUser, Model model) {
        var user = userService.getUserProfile(currentUser.getId());
        var request = com.springride.dto.ProfileRequest.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .phone(user.getPhone())
                .build();
        model.addAttribute("profileRequest", request);
        return "user/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@AuthenticationPrincipal User currentUser,
            @ModelAttribute com.springride.dto.ProfileRequest request) {
        userService.updateProfile(currentUser.getId(), request);
        return "redirect:/profile";
    }
}

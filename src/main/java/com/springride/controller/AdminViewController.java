package com.springride.controller;

import com.springride.model.Report;
// import com.springride.model.Report.ReportStatus; // Removed
import com.springride.model.Trip;
import com.springride.model.User;
import com.springride.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminViewController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Prepare stats for dashboard
        List<User> users = adminService.getAllUsers();
        List<Trip> trips = adminService.getAllTrips();
        List<Report> reports = adminService.getAllReports();
        List<com.springride.model.Reservation> reservations = adminService.getAllReservations();

        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalTrips", trips.size());
        model.addAttribute("totalReports", reports.size());
        model.addAttribute("totalReservations", reservations.size());

        // Show recent users (limit to 5, sorting could be added if CreatedAt existed,
        // assuming ID order for now)
        // ideally we would sort by id desc
        List<User> recentUsers = users.stream()
                .sorted((u1, u2) -> u2.getId().compareTo(u1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentUsers", recentUsers);

        return "admin/dashboard";
    }

    // --- User Management ---

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Statut de l'utilisateur mis à jour avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du statut.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression de l'utilisateur.");
        }
        return "redirect:/admin/users";
    }

    // --- Report Management ---

    @GetMapping("/reports")
    public String listReports(@RequestParam(required = false) Report.ReportStatus status, Model model) {
        if (status != null) {
            model.addAttribute("reports", adminService.getReportsByStatus(status));
            model.addAttribute("currentStatus", status);
        } else {
            model.addAttribute("reports", adminService.getAllReports());
        }
        return "admin/reports";
    }

    @PostMapping("/reports/{id}/status")
    public String updateReportStatus(@PathVariable Long id, @RequestParam Report.ReportStatus status,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.updateReportStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Statut du signalement mis à jour.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du signalement.");
        }
        return "redirect:/admin/reports";
    }

    @PostMapping("/users/{id}/warn")
    public String warnUser(@PathVariable Long id, @RequestParam String message, RedirectAttributes redirectAttributes) {
        try {
            adminService.sendWarning(id, message);
            redirectAttributes.addFlashAttribute("successMessage", "Avertissement envoyé à l'utilisateur.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'envoi de l'avertissement.");
        }
        return "redirect:/admin/reports"; // Remain on reports page as likely triggered from there
    }

    // --- Trip Management ---

    @GetMapping("/trips")
    public String listTrips(Model model) {
        model.addAttribute("trips", adminService.getAllTrips());
        return "admin/trips";
    }

    @PostMapping("/trips/{id}/delete")
    public String deleteTrip(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteTrip(id);
            redirectAttributes.addFlashAttribute("successMessage", "Trajet supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du trajet.");
        }
        return "redirect:/admin/trips";
    }
}

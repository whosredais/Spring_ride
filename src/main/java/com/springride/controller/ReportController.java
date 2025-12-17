package com.springride.controller;

import com.springride.model.User;
import com.springride.service.ReportService;
import com.springride.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    // --- User Endpoints ---

    @PostMapping("/submit")
    public String submitReport(@RequestParam Long reportedUserId,
            @RequestParam(required = false) Long tripId, // Optional trip
            @RequestParam String reason,
            Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer) {
        try {
            User reporter = userService.getUserByEmail(principal.getName());
            reportService.createReport(reportedUserId, tripId, reason, reporter);
            redirectAttributes.addFlashAttribute("successMessage", "Votre signalement a bien été pris en compte.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la soumission du signalement : " + e.getMessage());
        }
        return "redirect:" + (referer != null ? referer : "/");
    }

    @GetMapping("/my-reports")
    public String myReports(Principal principal, Model model) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("reports", reportService.getReportsByUser(user));
        return "user/complaints";
    }

    // --- Admin Endpoints ---

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String allReports(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "admin/complaints";
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public String resolveReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.resolveReport(id);
        redirectAttributes.addFlashAttribute("successMessage", "Signalement marqué comme résolu.");
        return "redirect:/reports/admin";
    }

    @PostMapping("/{id}/dismiss")
    @PreAuthorize("hasRole('ADMIN')")
    public String dismissReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.dismissReport(id);
        redirectAttributes.addFlashAttribute("successMessage", "Signalement rejeté.");
        return "redirect:/reports/admin";
    }

    @PostMapping("/{id}/warn")
    @PreAuthorize("hasRole('ADMIN')")
    public String warnUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            com.springride.model.Report report = reportService.getAllReports().stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Signalement non trouvé"));

            userService.warnUser(report.getReportedUser().getId());
            reportService.resolveReport(id); // Auto-resolve when warned

            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur averti avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur: " + e.getMessage());
        }
        return "redirect:/reports/admin";
    }
}

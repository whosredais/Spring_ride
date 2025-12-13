package com.springride.controller;

import com.springride.model.User;
import com.springride.service.ReportService;
import com.springride.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @PostMapping("/submit")
    public String submitReport(@RequestParam Long reportedUserId,
            @RequestParam String reason,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            User reporter = userService.getUserByEmail(principal.getName());
            reportService.createReport(reportedUserId, reason, reporter);
            redirectAttributes.addFlashAttribute("successMessage", "Votre signalement a bien été pris en compte.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la soumission du signalement : " + e.getMessage());
        }

        // Redirect back to referring page or defaulting to home.
        // ideally we should pass a redirect URL or just back
        return "redirect:/";
    }
}

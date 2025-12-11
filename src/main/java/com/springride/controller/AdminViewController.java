package com.springride.controller;

import com.springride.service.TripService;
import com.springride.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final UserService userService;
    private final TripService tripService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // TODO: In a real app, we would have a specific AdminService to get global
        // stats.
        // For now, we will just show empty stats or mock them in the template,
        // or add methods to existing services if needed.

        // For demo purposes, we'll just render the template.
        return "admin/dashboard";
    }
}

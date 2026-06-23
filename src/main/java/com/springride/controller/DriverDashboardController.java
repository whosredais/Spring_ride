package com.springride.controller;

import com.springride.model.User;
import com.springride.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverDashboardController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User currentUser, Model model) {
        var dashboard = userService.getUserDashboard(currentUser.getId());
        model.addAttribute("dashboard", dashboard);
        return "driver/dashboard";
    }
}

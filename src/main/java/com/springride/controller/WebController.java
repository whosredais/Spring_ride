package com.springride.controller;

import com.springride.dto.LoginRequest;
import com.springride.dto.RegisterRequest;
import com.springride.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final AuthService authService;

    @ModelAttribute("cities")
    public java.util.List<String> cities() {
        return com.springride.util.CityUtils.MOROCCAN_CITIES;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("registerRequest") RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return "redirect:/login";
    }

    @GetMapping("/switch-mode")
    public String switchMode(jakarta.servlet.http.HttpSession session,
            jakarta.servlet.http.HttpServletRequest request) {
        String currentMode = (String) session.getAttribute("userMode");
        if ("DRIVER".equals(currentMode)) {
            session.setAttribute("userMode", "PASSENGER");
            return "redirect:/search";
        } else {
            session.setAttribute("userMode", "DRIVER");
            return "redirect:/driver/dashboard";
        }
    }
}

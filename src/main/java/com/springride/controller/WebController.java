package com.springride.controller;

import com.springride.dto.RegisterRequest;
import com.springride.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("registerRequest") RegisterRequest registerRequest,
            RedirectAttributes redirectAttributes) {
        try {
            authService.register(registerRequest);
            redirectAttributes.addFlashAttribute("success", "Compte créé avec succès. Veuillez vérifier votre email.");
            return "redirect:/verify-account?email=" + registerRequest.getEmail();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/verify-account")
    public String verifyAccount(Model model, @RequestParam(required = false) String email) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "auth/verify-account";
    }

    @PostMapping("/verify-account")
    public String processVerifyAccount(@RequestParam String email, @RequestParam String code,
            RedirectAttributes redirectAttributes) {
        try {
            authService.verifyAccount(email, code);
            redirectAttributes.addFlashAttribute("success",
                    "Compte vérifié avec succès. Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Code invalide ou expiré.");
            return "redirect:/verify-account?email=" + email;
        }
    }

    @PostMapping("/resend-otp")
    public String processResendOtp(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            authService.resendOtp(email);
            redirectAttributes.addFlashAttribute("success", "Nouveau code envoyé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/verify-account?email=" + email;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            authService.forgotPassword(email);
            redirectAttributes.addFlashAttribute("success", "Code envoyé. Vérifiez votre email.");
            return "redirect:/reset-password?email=" + email;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model, @RequestParam(required = false) String email) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String email, @RequestParam String code,
            @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            authService.resetPassword(email, code, newPassword);
            redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès. Connectez-vous.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reset-password?email=" + email;
        }
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

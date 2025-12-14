package com.springride.controller;

import com.springride.dto.AuthResponse;
import com.springride.dto.LoginRequest;
import com.springride.dto.RegisterRequest;
import com.springride.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(new AuthResponse(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new AuthResponse(authService.login(request)));
    }

    @PostMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@RequestBody com.springride.dto.OtpVerificationRequest request) {
        authService.verifyAccount(request.getEmail(), request.getCode());
        return ResponseEntity.ok("Compte vérifié avec succès. Vous pouvez maintenant vous connecter.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody com.springride.dto.EmailRequest request) {
        authService.resendOtp(request.getEmail());
        return ResponseEntity.ok("Nouveau code envoyé.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody com.springride.dto.EmailRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Code de réinitialisation envoyé.");
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestBody com.springride.dto.OtpVerificationRequest request) {
        authService.verifyResetOtp(request.getEmail(), request.getCode());
        return ResponseEntity.ok("Code valide.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody com.springride.dto.ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok("Mot de passe modifié avec succès.");
    }
}
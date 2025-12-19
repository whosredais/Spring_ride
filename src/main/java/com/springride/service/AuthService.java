package com.springride.service;

import com.springride.dto.RegisterRequest;
import com.springride.model.User;
import com.springride.model.enums.Role;
import com.springride.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new com.springride.exception.BadRequestException("Cet email est déjà utilisé.");
        }

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .roles(Set.of(Role.PASSAGER, Role.CONDUCTEUR))
                .accountVerified(false)
                .build();
        userRepository.save(user);

        // Generate and send OTP
        otpService.generateAndSendOtp(user, com.springride.model.enums.OtpPurpose.ACCOUNT_VERIFICATION);
    }

    // Login is handled by Spring Security (formLogin), so we don't strictly need a
    // login method here
    // unless we want to do some extra checks. For now, we removed the manual JWT
    // generation.

    public void verifyAccount(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.springride.exception.BadRequestException("Utilisateur non trouvé"));

        if (otpService.validateOtp(user, code, com.springride.model.enums.OtpPurpose.ACCOUNT_VERIFICATION)) {
            user.setAccountVerified(true);
            userRepository.save(user);
        } else {
            throw new com.springride.exception.BadRequestException("Code OTP invalide ou expiré");
        }
    }

    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.springride.exception.BadRequestException("Utilisateur non trouvé"));
        if (user.isAccountVerified()) {
            throw new com.springride.exception.BadRequestException("Compte déjà vérifié");
        }
        otpService.generateAndSendOtp(user, com.springride.model.enums.OtpPurpose.ACCOUNT_VERIFICATION);
    }

    // Forgot Password Flow
    public void forgotPassword(String email) {
        log.info(">>> FORGOT PASSWORD called for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.springride.exception.BadRequestException("Utilisateur non trouvé"));
        log.info(">>> User found: {}, ID: {}, calling generateAndSendOtp", user.getEmail(), user.getId());
        otpService.generateAndSendOtp(user, com.springride.model.enums.OtpPurpose.PASSWORD_RESET);
        log.info(">>> generateAndSendOtp completed for {}", email);
    }

    public void verifyResetOtp(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.springride.exception.BadRequestException("Utilisateur non trouvé"));
        if (!otpService.validateOtp(user, code, com.springride.model.enums.OtpPurpose.PASSWORD_RESET)) {
            throw new com.springride.exception.BadRequestException("Code OTP invalide ou expiré");
        }
    }

    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.springride.exception.BadRequestException("Utilisateur non trouvé"));

        // We re-validate here to be safe, although one could argue we just need the
        // code to match what was sent.
        if (otpService.validateOtp(user, code, com.springride.model.enums.OtpPurpose.PASSWORD_RESET)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            // Ensure account is verified if they reset password successfully (implies they
            // own the email)
            user.setAccountVerified(true);
            userRepository.save(user);
        } else {
            throw new com.springride.exception.BadRequestException("Code OTP invalide ou expiré");
        }
    }
}

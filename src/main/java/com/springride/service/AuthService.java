package com.springride.service;

import com.springride.dto.LoginRequest;
import com.springride.dto.RegisterRequest;
import com.springride.model.User;
import com.springride.model.enums.Role;
import com.springride.repository.UserRepository;
import com.springride.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public String register(RegisterRequest request) {
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .roles(Set.of(Role.PASSAGER, Role.CONDUCTEUR))
                .accountVerified(false) // Explicitly set to false
                .build();
        userRepository.save(user);

        // Generate and send OTP
        otpService.generateAndSendOtp(user, com.springride.model.enums.OtpPurpose.ACCOUNT_VERIFICATION);

        // We return a token, but the user won't be able to use it effectively if we
        // enforce isEnabled check on specific endpoints
        // OR the frontend should handle the "Account created, please verify" state.
        // Actually, common pattern: don't return token yet, or return it but IsEnabled
        // prevents login.
        // Here we return String to match existing signature, but maybe return
        // "Registration successful. Please verify email."
        // logic is handled by controller generally.
        // For backward compatibility with existing code flow, we return token, but
        // since isEnabled() is false,
        // subsequent requests might fail if they check user status.
        // HOWEVER, `login` checks authenticationManager which checks `isEnabled`.
        return jwtService.generateToken(user);
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // At this point, authenticationManager has already checked isEnabled() via
        // UserDetails.
        // So if accountVerified is false, authentication throws DisabledException or
        // similar.

        User user = userRepository.findByEmail(request.getUsername()).orElseThrow();
        return jwtService.generateToken(user);
    }

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

        if (otpService.validateOtp(user, code, com.springride.model.enums.OtpPurpose.PASSWORD_RESET)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setAccountVerified(true);
            userRepository.save(user);
        } else {
            throw new com.springride.exception.BadRequestException("Code OTP invalide ou expiré");
        }
    }
}
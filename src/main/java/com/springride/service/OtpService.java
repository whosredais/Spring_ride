package com.springride.service;

import com.springride.model.Otp;
import com.springride.model.User;
import com.springride.model.enums.OtpPurpose;
import com.springride.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    private static final int OTP_VALIDITY_MINUTES = 3;

    @Transactional
    public void generateAndSendOtp(User user, OtpPurpose purpose) {
        log.info("=== START generateAndSendOtp for user: {}, purpose: {}", user.getEmail(), purpose);

        // Invalidate existing active OTPs for this user and purpose
        java.util.List<Otp> existingOtps = otpRepository.findByUserAndPurposeAndUsedFalse(user, purpose);
        if (!existingOtps.isEmpty()) {
            log.info("Invalidating {} existing active OTPs for user: {}", existingOtps.size(), user.getEmail());
            existingOtps.forEach(otp -> {
                otp.setUsed(true); // Or you could mark them as expired if you had a status field
                // If you want to be strict about "used" vs "expired", we can keep used=true as
                // "invalidated"
            });
            otpRepository.saveAll(existingOtps);
        }

        // Generate Code
        String code = String.format("%06d", new Random().nextInt(999999));

        log.info("Generated OTP code: {} for user: {}", code, user.getEmail());

        // Save to DB
        Otp otp = Otp.builder()
                .code(code)
                .user(user)
                .purpose(purpose)
                .expirationDate(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .used(false)
                .build();

        log.info("Built OTP object - code: {}, expiration: {}", otp.getCode(), otp.getExpirationDate());

        Otp savedOtp = otpRepository.save(otp);

        log.info("Saved OTP to database with ID: {}, code: {}", savedOtp.getId(), savedOtp.getCode());

        // Send Email
        String subject = purpose == OtpPurpose.ACCOUNT_VERIFICATION ? "Vérification de votre compte SpringRide"
                : "Réinitialisation de mot de passe";
        String message = "Votre code de vérification est : " + code + "\n\nCe code expire dans " + OTP_VALIDITY_MINUTES
                + " minutes.";

        log.info("Sending email to {} with OTP code: {}", user.getEmail(), code);
        emailService.sendEmail(user.getEmail(), subject, message);
        log.info("=== END generateAndSendOtp");
    }

    @Transactional
    public boolean validateOtp(User user, String code, OtpPurpose purpose) {
        log.info("Validating OTP for user {} with code {} and purpose {}", user.getEmail(), code, purpose);

        Optional<Otp> otpOpt = otpRepository.findValidOtp(user, code, purpose, LocalDateTime.now());

        if (otpOpt.isPresent()) {
            Otp otp = otpOpt.get();
            log.info("OTP found - expiration: {}, used: {}, current time: {}",
                    otp.getExpirationDate(), otp.isUsed(), LocalDateTime.now());
            otp.setUsed(true);
            otpRepository.save(otp);
            return true;
        } else {
            log.warn("No valid OTP found for user {} with code {} and purpose {}", user.getEmail(), code, purpose);
            return false;
        }
    }
}

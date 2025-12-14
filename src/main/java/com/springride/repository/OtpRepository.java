package com.springride.repository;

import com.springride.model.Otp;
import com.springride.model.User;
import com.springride.model.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    @Query("SELECT o FROM Otp o WHERE o.user = :user AND o.code = :code AND o.purpose = :purpose AND o.used = false AND o.expirationDate > :now")
    Optional<Otp> findValidOtp(@Param("user") User user, @Param("code") String code,
            @Param("purpose") OtpPurpose purpose, @Param("now") java.time.LocalDateTime now);

    // Find all active OTPs to invalidate them
    java.util.List<Otp> findByUserAndPurposeAndUsedFalse(User user, OtpPurpose purpose);
}

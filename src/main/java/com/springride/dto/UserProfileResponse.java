package com.springride.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
        private Long id;
        private String firstname;
        private String lastname;
        private String email;
        private String phone;
        private double averageRating;
        private int reviewCount;
        // On pourrait ajouter date d'inscription si dispo
}
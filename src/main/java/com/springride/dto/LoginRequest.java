package com.springride.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
        @NotBlank(message = "L'email est requis")
        @Email(message = "Format d'email invalide")
        private String username; // Changed from email to username to match Spring conventions often, or just
                                 // keep email but standard formLogin maps 'username' param by default

        @NotBlank(message = "Le mot de passe est requis")
        private String password;
}
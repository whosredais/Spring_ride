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
public class RegisterRequest {
        @NotBlank(message = "Le prénom est requis")
        private String firstname;

        @NotBlank(message = "Le nom est requis")
        private String lastname;

        @NotBlank(message = "L'email est requis")
        @Email(message = "Format d'email invalide")
        private String email;

        @NotBlank(message = "Le mot de passe est requis")
        private String password;

        private String phone;
}

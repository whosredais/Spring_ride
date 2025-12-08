// src/main/java/com/springride/config/DataInitializer.java
package com.springride.config;

import com.springride.model.User;
import com.springride.model.enums.Role;
import com.springride.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;   // S’exécute au démarrage
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component                     // Spring va détecter cette classe automatiquement
@RequiredArgsConstructor      // Crée un constructeur avec les dépendances (userRepository, passwordEncoder)
public class DataInitializer implements CommandLineRunner {  // → run() s’exécute au lancement

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;   // Pour crypter le mot de passe

    @Override
    public void run(String... args) throws Exception {
        // Si l’admin n’existe pas encore
        if (!userRepository.existsByEmail("admin@springride.com")) {

            // On crée l’admin avec tous les pouvoirs
            User admin = User.builder()
                    .firstname("Admin")
                    .lastname("SpringRide")
                    .email("admin@springride.com")
                    .password(passwordEncoder.encode("admin123"))   // Mot de passe crypté
                    .phone("0600000000")
                    .roles(Set.of(Role.ADMIN, Role.CONDUCTEUR, Role.PASSAGER))  // Il peut tout faire
                    .build();

            userRepository.save(admin);

            // Message dans la console pour te prévenir
            System.out.println("ADMIN créé → email: admin@springride.com | mot de passe: admin123");
        }
    }
}
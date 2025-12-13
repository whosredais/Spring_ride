// src/main/java/com/springride/config/DataInitializer.java
package com.springride.config;

import com.springride.model.User;
import com.springride.model.enums.Role;
import com.springride.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner; // S’exécute au démarrage
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component // Spring va détecter cette classe automatiquement
@RequiredArgsConstructor // Crée un constructeur avec les dépendances (userRepository, passwordEncoder)
public class DataInitializer implements CommandLineRunner { // → run() s’exécute au lancement

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Pour crypter le mot de passe

    @Override
    public void run(String... args) throws Exception {
        // 1. Initialisation par défaut (admin@springride.com)
        if (!userRepository.existsByEmail("admin@springride.com")) {
            User admin = User.builder()
                    .firstname("Admin")
                    .lastname("SpringRide")
                    .email("admin@springride.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("0600000000")
                    .roles(Set.of(Role.ADMIN, Role.CONDUCTEUR, Role.PASSAGER))
                    .active(true)
                    .build();
            userRepository.save(admin);
            System.out.println("ADMIN créé → email: admin@springride.com | mot de passe: admin123");
        } else {
            // Ensure it is active if it exists
            userRepository.findByEmail("admin@springride.com").ifPresent(admin -> {
                if (!admin.isActive()) {
                    admin.setActive(true);
                    userRepository.save(admin);
                    System.out.println("Compte ADMIN (springride) réactivé.");
                }
            });
        }

        // 2. Fix spécifique pour l'utilisateur existant "admin@spring.com"
        userRepository.findByEmail("admin@spring.com").ifPresent(user -> {
            boolean changed = false;
            // Ensure ACTIVE
            if (!user.isActive()) {
                user.setActive(true);
                changed = true;
                System.out.println("Compte 'admin@spring.com' réactivé.");
            }
            // Ensure ROLE_ADMIN (just in case)
            if (!user.getRoles().contains(Role.ADMIN)) {
                user.getRoles().add(Role.ADMIN);
                changed = true;
                System.out.println("Role ADMIN ajouté à 'admin@spring.com'.");
            }

            if (changed) {
                userRepository.save(user);
            }
        });
    }
}
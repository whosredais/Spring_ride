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
                    .roles(Set.of(Role.ADMIN))
                    .active(true)
                    .accountVerified(true)
                    .build();
            userRepository.save(admin);
            System.out.println("ADMIN créé → email: admin@springride.com | mot de passe: admin123");
        } else {
            // Ensure it is active and has strictly ADMIN role
            userRepository.findByEmail("admin@springride.com").ifPresent(admin -> {
                boolean changed = false;
                if (!admin.isActive()) {
                    admin.setActive(true);
                    changed = true;
                    System.out.println("Compte ADMIN (springride) réactivé.");
                }
                if (!admin.isAccountVerified()) {
                    admin.setAccountVerified(true);
                    changed = true;
                    System.out.println("Compte ADMIN (springride) vérifié.");
                }
                // Enforce STRICTLY ADMIN role (remove others)
                if (admin.getRoles().size() != 1 || !admin.getRoles().contains(Role.ADMIN)) {
                    admin.setRoles(Set.of(Role.ADMIN));
                    changed = true;
                    System.out.println("Roles ADMIN corrigés (ADMIN unique).");
                }

                if (changed) {
                    userRepository.save(admin);
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
            // Enforce STRICTLY ADMIN role
            if (user.getRoles().size() != 1 || !user.getRoles().contains(Role.ADMIN)) {
                user.setRoles(Set.of(Role.ADMIN));
                changed = true;
                System.out.println("Roles mis à jour pour 'admin@spring.com' -> ADMIN unique.");
            }

            if (changed) {
                userRepository.save(user);
            }
        });
    }
}
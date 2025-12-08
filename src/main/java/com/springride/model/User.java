// src/main/java/com/springride/model/User.java
package com.springride.model;

import com.springride.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// @Entity = cette classe va devenir une table dans la base de données
@Entity
// Nom de la table dans MySQL/H2
@Table(name = "users")
@Data                   // Génère automatiquement getters, setters, toString, equals, hashCode (grâce à Lombok)
@NoArgsConstructor      // Constructeur vide (obligatoire pour JPA)
@AllArgsConstructor     // Constructeur avec tous les champs
@Builder                // Permet de créer un objet facilement → User.builder().email("...").build()
public class User implements UserDetails {  // UserDetails = obligatoire pour Spring Security

    @Id                                                 // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrément (1, 2, 3...)
    private Long id;

    @Column(nullable = false)       // Champ obligatoire en base
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)    // Email unique → pas deux comptes avec même email
    private String email;

    @Column(nullable = false)
    private String password;        // Mot de passe hashé (jamais en clair !)

    private String phone;           // Optionnel

    // Note moyenne (ex: 4.8/5)
    private Double averageRating = 0.0;
    private Integer reviewCount = 0;

    // Un utilisateur peut avoir plusieurs rôles en même temps
    @ElementCollection(fetch = FetchType.EAGER)  // Charge les rôles dès qu’on charge l’utilisateur
    @Enumerated(EnumType.STRING)                 // Stocké comme texte ("ADMIN", "CONDUCTEUR"...) → lisible en base
    private Set<Role> roles = new HashSet<>();

    // === Méthodes obligatoires pour Spring Security ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertit chaque rôle en "ROLE_ADMIN", "ROLE_CONDUCTEUR" etc.
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }
  @Override
public String getPassword() {
    return this.password; // ou le champ où tu stockes le mot de passe
}
    @Override public String getUsername() { return email; }        // Login = email
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
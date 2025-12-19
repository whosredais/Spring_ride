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

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    private String profilePicture;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private Integer reviewCount = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    private boolean accountVerified = false;

    @Builder.Default
    private Integer strikes = 0;

    private String warningMessage; // Message d'avertissement de l'admin

    // === Méthodes obligatoires pour Spring Security ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // User must be both active (admin control) and verified (email)
        return this.active && this.accountVerified;
    }

    // === Relations pour suppression en cascade ===

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Trip> trips = new HashSet<>();

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Reservation> reservations = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Vehicle> vehicles = new HashSet<>();

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Report> sentReports = new HashSet<>();

    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Report> receivedReports = new HashSet<>();

    @OneToMany(mappedBy = "reviewer")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Review> writtenReviews = new HashSet<>();

    @OneToMany(mappedBy = "reviewed")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Review> receivedReviews = new HashSet<>();
}
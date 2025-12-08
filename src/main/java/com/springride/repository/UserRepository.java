// 1. UserRepository.java
package com.springride.repository;

import com.springride.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Pour le login

    boolean existsByEmail(String email); // Pour vérifier si l’email existe déjà
}
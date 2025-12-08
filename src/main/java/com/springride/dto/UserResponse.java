// dto/UserResponse.java (pour plus tard)
package com.springride.dto;

public record UserResponse(
        Long id,
        String firstname,
        String lastname,
        String email,
        String phone) {
}
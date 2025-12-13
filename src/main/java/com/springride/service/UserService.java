package com.springride.service;

import com.springride.dto.DashboardResponse;
import com.springride.dto.UserResponse;

public interface UserService {
    UserResponse getUserProfile(Long userId);

    DashboardResponse getUserDashboard(Long userId);

    void updateProfile(Long userId, com.springride.dto.ProfileRequest request);

    com.springride.model.User getUserByEmail(String email);
}

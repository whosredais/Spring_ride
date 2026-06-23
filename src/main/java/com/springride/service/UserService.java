package com.springride.service;

import com.springride.dto.DashboardResponse;
import com.springride.dto.UserResponse;
import com.springride.model.User;
import com.springride.dto.ProfileRequest;

public interface UserService {
    UserResponse getUserProfile(Long userId);

    DashboardResponse getUserDashboard(Long userId);

    void updateProfile(Long userId, ProfileRequest request);

    User getUserByEmail(String email);

    void warnUser(Long userId);
}

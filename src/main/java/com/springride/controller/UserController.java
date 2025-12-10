package com.springride.controller;

import com.springride.dto.DashboardResponse;
import com.springride.dto.UserResponse;
import com.springride.model.User;
import com.springride.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getUserProfile(currentUser.getId()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getMyDashboard(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getUserDashboard(currentUser.getId()));
    }
}

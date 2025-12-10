package com.springride.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

@Configuration
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            request.getSession().setAttribute("userMode", "ADMIN");
            response.sendRedirect("/dashboard");
        } else if (roles.contains("ROLE_CONDUCTEUR")) {
            request.getSession().setAttribute("userMode", "DRIVER"); // Default for drivers? or let them choose? Let's
                                                                     // say DRIVER.
            response.sendRedirect("/dashboard");
        } else {
            // ROLE_PASSAGER
            request.getSession().setAttribute("userMode", "PASSENGER");
            response.sendRedirect("/search");
        }
    }
}

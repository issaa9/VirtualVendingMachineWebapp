package com.example.finalyearproject.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//custom success handler class to handle navigating to the right page after successful login, based on user role
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));  //check if the user logging in is an admin

        if (isAdmin) {
            response.sendRedirect("/admin/dashboard");  //if the user is an admin, redirect to the admin dashboard
        } else {
            response.sendRedirect("/home"); //if not redirect them to the homepage
        }
    }
}

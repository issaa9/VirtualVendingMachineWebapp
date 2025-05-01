package com.example.finalyearproject.security;

import com.example.finalyearproject.model.User;
import com.example.finalyearproject.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

//custom success handler class to handle navigating to the right page after successful login, based on user role
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) { //if they are an OAuth2 user
            //fetch the email and name
            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            Optional<User> existingUser = userRepo.findByEmail(email); //attempt to find the user by email
            String finalUsername;

            if (existingUser.isEmpty()) {  //if the user doesn't already exist, they need to be registered
                //generate a new unique username based on their display name
                String baseUsername = name.replaceAll("\\s+", ""); //remove all spaces
                String username = baseUsername; //set as a base username
                int suffix = 1;  //initialise suffix counter

                //ensure username is unique by adding on a relevant suffix to the end
                while (userRepo.findByUsername(username).isPresent()) {  //while the base username already exists
                    username = baseUsername + suffix; //add the suffix on
                    suffix++;  //increment the suffix
                }

                //create new user entry with the generated username, the user's email, a blank password and set the role to 'USER'
                User newUser = new User(username, email, "", "USER");

                userRepo.save(newUser);  //save the user to the database

                finalUsername = username;  //set the final username

            } else {

                finalUsername = existingUser.get().getUsername(); //use existing username for finl username
            }

            //if the user does already exist, then log them in
            request.getSession().setAttribute("username", finalUsername); //set the username in the session, using final username
            response.sendRedirect("/home");  //redirect to homepage
            return;  //return from the method to stop further processing, OAuth2 users cant be admins (yet)
        }


        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));  //check if the user logging in is an admin

        request.getSession().setAttribute("username", authentication.getName());
        if (isAdmin) {
            response.sendRedirect("/admin/dashboard");  //if the user is an admin, redirect to the admin dashboard
        } else {
            response.sendRedirect("/home"); //if not redirect them to the homepage
        }
    }
}

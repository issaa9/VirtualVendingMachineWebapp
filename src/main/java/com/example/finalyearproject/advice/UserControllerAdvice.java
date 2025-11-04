package com.example.finalyearproject.advice;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

//controller advice allows applying of common behaviour to multiple controllers at the same time
@ControllerAdvice //applies to all controller classes
public class UserControllerAdvice {

    //method to make controller methods add the username attribute in to their models to display the user info on their respective pages
    @ModelAttribute("username")
    public String addUsernameToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "Guest"; //if not authenticated or anonymous user, return Guest as the username
        }

        Object principal = authentication.getPrincipal();

        //for standard logins
        if (principal instanceof org.springframework.security.core.userdetails.User) {  //if logged in with a user account from the user model
            return ((org.springframework.security.core.userdetails.User) principal).getUsername(); //return the username
        }

        //for OAuth2 login
        if (principal instanceof OAuth2User oauthUser) { //if logged in using OAuth2

            //try retrieving the name and returning
            String name = oauthUser.getAttribute("name");
            if (name != null && !name.isEmpty()) {  //only return if name is not null or empty
                return name;
            }

            //otherwise try retrieving the email
            String email = oauthUser.getAttribute("email");
            if (email != null) {  //only return if email is not null
                return email.split("@")[0]; //extract only the email prefix (part before the '@')
            }

            return "GoogleUser";  //fallback in case any issues
        }

        return authentication.getName(); //final fallback for all checks
    }
}

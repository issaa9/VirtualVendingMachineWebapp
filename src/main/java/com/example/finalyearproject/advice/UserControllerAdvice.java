package com.example.finalyearproject.advice;

import com.example.finalyearproject.controller.MainController;
import com.example.finalyearproject.controller.NavigationController;
import com.example.finalyearproject.controller.ReceiptController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

//controller advice allows applying of common behaviour to multiple controllers at the same time
@ControllerAdvice(basePackageClasses = {NavigationController.class, MainController.class, ReceiptController.class}) //applies only to these controller classes
public class UserControllerAdvice {

    //method to make controller methods add the username attribute in to their models to display the user info on their respective pages
    @ModelAttribute("username")
    public String addUsernameToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {  //check if authenticated and not an anonymous user
            return authentication.getName(); //return the username
        } else {
            return "Guest";  //return "Guest" when no authenticated user is found or logged in as "anonymousUser"
        }
    }
}

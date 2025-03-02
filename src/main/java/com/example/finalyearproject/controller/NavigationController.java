package com.example.finalyearproject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    @GetMapping("/home")
    public String showHomePage() {
        return "homepage"; //render the homepage
    }

    @GetMapping("/about")
    public String showAboutPage() {

        return "aboutpage"; //render the about page
    }

    @GetMapping("/instructions")
    public String showInstructionsPage() {

        return "instructionspage"; //render the instructions page
    }
}

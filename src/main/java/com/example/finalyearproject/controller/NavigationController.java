package com.example.finalyearproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    @GetMapping("/")
    public String showHomePage() {
        return "homepage"; // Return the homepage template
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "aboutpage"; // Return the about page template
    }

    @GetMapping("/instructions")
    public String showInstructionsPage() {
        return "instructionspage"; // Return the instructions page template
    }
}

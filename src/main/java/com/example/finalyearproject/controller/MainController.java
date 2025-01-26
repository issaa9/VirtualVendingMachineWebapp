package com.example.finalyearproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/main")
    public String showMainPage(Model model) {
        // Add any necessary attributes for the main page
        model.addAttribute("welcomeMessage", "Welcome to the Virtual Vending Machine!");
        return "mainpage"; // Return the main page template
    }
}

package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.User;
import com.example.finalyearproject.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class RegisterController {

    @Autowired
    private UserRepo userRepository;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; //show register page
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        //check if user already exists
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username or email already exists.");  //error message for already existing user
            return "register";  //redirect to register page
        }

        String defaultRole = "user";
        User newUser = new User(username, email, password, defaultRole);
        userRepository.save(newUser); //save new user to database

        return "redirect:/login"; //redirect to login page
    }
}

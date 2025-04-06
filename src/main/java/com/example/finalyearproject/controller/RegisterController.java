package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.User;
import com.example.finalyearproject.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/register")
    public String showRegisterPage() {
        return "registerpage"; //show register page
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByUsername(username).isPresent()) { // check if username or email already exists
            model.addAttribute("error", "Username or email already exists."); //display error message
            return "registerpage";  //redisplay register page
        }

        if (!password.equals(confirmPassword)) {  //checking password matches confirm password
            model.addAttribute("error", "Passwords do not match."); //display message
            return "registerpage";  //redisplay page
        }


        String hashedPassword = passwordEncoder.encode(password);  //encrypt password
        User newUser = new User(username, email, hashedPassword, "USER");  //create user entry
        userRepository.save(newUser);  //save in DB

        return "redirect:/login";  //display login page after successfully registering
    }
}

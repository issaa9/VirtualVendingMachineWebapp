package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.User;
import com.example.finalyearproject.repository.UserRepo;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    private UserRepo userRepository;

    @GetMapping("/login")
    public String showLoginPage(HttpSession session, HttpServletResponse response) {
        session.invalidate(); //always clear session to enforce fresh entry everytime login page is accessed
        return "loginpage"; //show login page
    }

    //handle guest logins
    @PostMapping("/guest-login")
    public String loginAsGuest(HttpSession session) {
        session.setAttribute("username", "Guest"); // manually set session attribute
        return "redirect:/home"; // redirect to home page as guest
    }

//
//    @PostMapping("/login")
//    public String loginUser(@RequestParam String identifier, @RequestParam String password, HttpSession session, Model model) {
//        Optional<User> userOpt = userRepository.findByEmailOrUsername(identifier, identifier);
//
//        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
//            session.setAttribute("user", userOpt.get());  //store user session
//            return "redirect:/home"; //redirect to home page
//        }
//
//        model.addAttribute("error", "Invalid email/username or password.");
//        return "loginpage";  //redisplay login page with error message
//    }
//
    //controller logout method
    @GetMapping("/log-out")
    public String logout(HttpSession session) {
        session.invalidate();  //clear session
        return "redirect:/login";  //redisplay login page
    }
}

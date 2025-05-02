package com.example.finalyearproject.controller;


import com.example.finalyearproject.repository.UserRepo;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


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

    //controller logout method
    @GetMapping("/log-out")
    public String logout(HttpSession session) {
        session.invalidate();  //clear session
        return "redirect:/login";  //redisplay login page
    }
}

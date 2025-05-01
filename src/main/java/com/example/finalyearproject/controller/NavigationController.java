package com.example.finalyearproject.controller;

import com.example.finalyearproject.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session) {

        //if not logged in or guest browsing, don't allow access and redirect back to login
        Object username = session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        double totalTakings = transactionService.getTotalTakings();
        model.addAttribute("totalTakings", totalTakings);
        return "homepage"; //render the homepage
    }

    @GetMapping("/transactions")
    public String showTransactionsPage(HttpSession session) {
        //if not logged in, don't allow access and redirect back to login (restricted from guests too)
        Object username = session.getAttribute("username");
        if (username == null || "Guest".equals(username)) {
            return "redirect:/login";
        }

        return "transactionspage"; //render the transactions history page
    }

    @GetMapping("/about")
    public String showAboutPage(HttpSession session) {

        //if not logged in or browsing as guest, don't allow access and redirect back to login
        Object username = session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        return "aboutpage"; //render the about page
    }

    @GetMapping("/instructions")
    public String showInstructionsPage(HttpSession session) {

        //if not logged in or browsing as guest, don't allow access and redirect back to login
        Object username = session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        return "instructionspage"; //render the instructions page
    }
}

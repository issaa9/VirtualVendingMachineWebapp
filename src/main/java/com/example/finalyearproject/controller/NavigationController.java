package com.example.finalyearproject.controller;

import com.example.finalyearproject.service.TransactionService;
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
    public String showHomePage(Model model) {
        double totalTakings = transactionService.getTotalTakings();
        model.addAttribute("totalTakings", totalTakings);
        return "homepage"; //render the homepage
    }

    @GetMapping("/transactions")
    public String showTransactionsPage() {
        return "transactionspage"; //render the transactions history page
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

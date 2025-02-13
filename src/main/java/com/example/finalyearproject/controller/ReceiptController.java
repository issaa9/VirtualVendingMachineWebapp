package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{transactionId}")
    public String showReceiptPage(@PathVariable Long transactionId, Model model) {
        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            model.addAttribute("transaction", transaction);
        } catch (Exception e) {
            model.addAttribute("error", "Transaction not found.");
        }
        return "receiptpage";  // Uses Thymeleaf
    }
}

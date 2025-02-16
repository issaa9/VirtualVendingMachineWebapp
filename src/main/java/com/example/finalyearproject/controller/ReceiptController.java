package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.model.TransactionProduct;
import com.example.finalyearproject.repository.ProductRepo;
import com.example.finalyearproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.finalyearproject.model.Product;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductRepo productRepo;


    @GetMapping("/{transactionId}")
    public String showReceiptPage(@PathVariable Long transactionId, Model model) {
        try {

            System.out.println("Fetching Transaction ID: " + transactionId);//logging

            Transaction transaction = transactionService.getTransactionById(transactionId);


            if (transaction == null) {
                System.out.println("ERROR: Transaction " + transactionId + " NOT FOUND in database!"); //logging
                model.addAttribute("error", "Transaction not found.");
                return "receiptpage";  //still render the page but with the error message
            }
            System.out.println("Transaction Retrieved: " + transaction);  //logging

            Map<String, String> productNames = new HashMap<>();  //mapping of product IDs to product names
            for (TransactionProduct tp : transaction.getTransactionProducts()) {
                String productId = tp.getProductId();  //product ID stored as a String
                String productName = productRepo.findById(productId)
                        .map(Product::getName)  //fetch product name if exists
                        .orElse("Unknown Product");  //handle missing product

                productNames.put(productId, productName); //add to hash map
            }

            //add transaction and product names hash map to model
            model.addAttribute("transaction", transaction);
            model.addAttribute("productNames", productNames);

        } catch (Exception e) {
            model.addAttribute("error", "Unexpected Error with Transaction"); //error message
        }
        return "receiptpage";  //renders receipt page
    }
}

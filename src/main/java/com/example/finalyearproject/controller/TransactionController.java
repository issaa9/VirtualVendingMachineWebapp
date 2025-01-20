package com.example.finalyearproject.controller;

import com.example.finalyearproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    //endpoint to test generating a receipt
    @GetMapping("/{transactionId}/receipt")
    public String getReceipt(@PathVariable Long transactionId) {  //retrieve id from url
        String receipt = transactionService.generateReceipt(transactionId);
        return "<pre style='font-size:30px;'>" + receipt + "</pre>";
        //pre tags used to display the receipt formatted and allow the size to be changed
    }
}

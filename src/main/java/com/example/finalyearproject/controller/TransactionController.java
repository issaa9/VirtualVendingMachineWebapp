package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    //endpoint to create and store the transaction from frontend
    @PostMapping("/create")
    public ResponseEntity <Map<String,String>> createTransaction(@RequestBody Map<String, Object> requestBody) {
        try {
            //extract item quantities
            @SuppressWarnings("unchecked")  //suppress the warning
            Map<String, Integer> productQuantities = (Map<String, Integer>) (Object) requestBody.get("productQuantities");

            //extract paymentReceived and store as a double
            double paymentReceived = ((Number) requestBody.get("paymentReceived")).doubleValue();

            //call service method to create the transaction
            Transaction transaction = transactionService.createTransaction(productQuantities, paymentReceived);

            //extract change and round (to prevent floating point error)
            double change = transactionService.roundTwoDP(transaction.getChangeGiven());

            //format the change as a 2.d.p string
            String formattedChange = String.format("%.2f", change);

            //creating map for JSON response
            Map<String, String> response = new HashMap<>();
            response.put("transactionId", String.valueOf(transaction.getId()));
            response.put("changeGiven", String.format("%.2f", transaction.getChangeGiven()));
            response.put("message", "Transaction successful! Change given: £" + String.format("%.2f", transaction.getChangeGiven()));

            //return JSON success response
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Transaction failed: " + e.getMessage())); //return failure response
        }
    }




    //endpoint to test generating a receipt
    @GetMapping("/{transactionId}/receipt")
    public String getReceipt(@PathVariable Long transactionId) {  //retrieve id from url
        String receipt = transactionService.generateReceipt(transactionId);
        return "<pre style='font-size:30px;'>" + receipt + "</pre>";
        //pre tags used to display the receipt formatted and allow the size to be changed
    }
}

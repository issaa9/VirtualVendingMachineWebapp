package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    //endpoint to create and store the transaction from frontend
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createTransaction(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("Received request body: " + requestBody);
            //extract item quantities
            @SuppressWarnings("unchecked")  //suppress the warning
            Map<String, Integer> productQuantities = (Map<String, Integer>) (Object) requestBody.get("productQuantities");

            //extract paymentReceived and store as a double
            double paymentReceived = ((Number) requestBody.get("paymentReceived")).doubleValue();

            //extract username from requestBody
            String username = (String) requestBody.get("username");
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username is required."));  //error handling in case of no username
            }

            //call service method to create the transaction
            Transaction transaction = transactionService.createTransaction(productQuantities, paymentReceived, username);

            //deduct stock and track restocked items
            List<String> restockedItems = transactionService.deductStock(productQuantities);

            //extract change and round (to prevent floating point error)
            double change = transactionService.roundTwoDP(transaction.getChangeGiven());

            //format the change as a 2.d.p string
            String formattedChange = String.format("%.2f", change);

            //creating map for JSON response
            Map<String, String> response = new HashMap<>();
            response.put("transactionId", String.valueOf(transaction.getId()));
            response.put("changeGiven", String.format("%.2f", transaction.getChangeGiven()));
            response.put("message", "Transaction successful! Change given: £" + String.format("%.2f", transaction.getChangeGiven()));


            //if restocked items list is not empty, add it to the response
            if (!restockedItems.isEmpty()) {
                String alertMessage = "Low Stock for: " + String.join(", ", restockedItems) + " has been detected and auto-restocked!";
                response.put("restockAlert", alertMessage);
            }

            //return JSON success response
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Transaction failed: " + e.getMessage()));  //return failure response
        }
    }

    @GetMapping("/user")   //api endpoint to fetch all transactions for the user
    public ResponseEntity<List<Transaction>> getUserTransactions(@ModelAttribute("username") String username) { //model attribute used to retrieve the username of the user currently logged in

        //block guests from trying to fetch transactions (blocked on frontend but failsafe for extra security)
        if ("Guest".equalsIgnoreCase(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
        }

        List<Transaction> transactions = transactionService.getTransactionsByUsername(username); //call service method to retrieve transactions
        return ResponseEntity.ok(transactions);  //return transaction list
    }


    @GetMapping("/filter")  //api endpoint to filter/query transactions
    public ResponseEntity<List<Transaction>> filterTransactions(
            @RequestParam(required = false) Long transactionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,   //formatted dates
            @RequestParam(required = false) Double minTotalCost,
            @RequestParam(required = false) Double maxTotalCost,
            @RequestParam(required = false) Double minPayment,
            @RequestParam(required = false) Double maxPayment,
            @RequestParam(required = false) Double minChange,
            @RequestParam(required = false) Double maxChange,
            @RequestParam String username) {
            //request all possible parameters, they aren't all required because it allows null values, only username is required

        //block guests from trying to fetch transactions (blocked on frontend but failsafe for extra security)
        if ("Guest".equalsIgnoreCase(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
        }

        List<Transaction> filteredTransactions = transactionService.getFilteredTransactions(   //call service method and pass in the parameters, if some have no value NULL will be sent for them
                transactionId, startDate, endDate, minTotalCost, maxTotalCost, minPayment, maxPayment, minChange, maxChange, username
        );

        return ResponseEntity.ok(filteredTransactions);  //return the filtered transactions list as JSON response to frontend
    }



}
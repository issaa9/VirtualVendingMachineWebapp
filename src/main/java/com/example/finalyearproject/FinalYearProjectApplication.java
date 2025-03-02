package com.example.finalyearproject;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.service.TransactionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.finalyearproject.service.ProductService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootApplication
public class FinalYearProjectApplication {

    @Autowired
    private ProductService productService;

    @Autowired
    private TransactionService transactionService;

    public static void main(String[] args) {

        SpringApplication.run(FinalYearProjectApplication.class, args);
    }
    //testing adding a product to the DB
    public void addProductAndPrint() {
        // call the addProduct method from ProductService to test that it works
        Product product = productService.addProduct("D5","Test Product", 15.99, 50);

        // print out the product details  to ensure they are stored correctly
        System.out.println("Product added: ");
        System.out.println("ID: " + product.getId());
        System.out.println("Name: " + product.getName());
        System.out.println("Price: £" + String.format("%.2f", product.getPrice()));
        System.out.println("Stock Level: " + product.getStock());
    }

   //testing updating stock for a product
    public void updateStockAndPrint(){
        productService.updateStock("D5", 10);  // testing adding 10 to stock
        Product product = productService.getProductById("D5");
        System.out.println("Stock Level: " + product.getStock());

        productService.updateStock("D5", 15);  // testing again by adding another 15 to stock
        Product product1 = productService.getProductById("D5");
        System.out.println("Stock Level: " + product1.getStock());
    }

    //test creating a transaction
// Test creating a transaction with quantities
    public void createTransactionTest() {
        // Create a test transaction with product IDs and their quantities
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("A1", 2); // Buy 2 of A1
        productQuantities.put("A2", 1); // Buy 1 of A2
        productQuantities.put("B1", 3); // Buy 3 of B1

        double paymentReceived = 20.00; // Simulated payment

        try {
            // Create the transaction
            Transaction transaction = transactionService.createTransaction(productQuantities, paymentReceived);

            // Print out transaction details
            System.out.println("Transaction created successfully:");
            System.out.println("Total Cost: £" + String.format("%.2f", transaction.getTotalCost()));
            System.out.println("Payment Received: £" + String.format("%.2f", transaction.getPaymentReceived()));
            System.out.println("Change Given: £" + String.format("%.2f", transaction.getChangeGiven()));

            // Print out updated stock levels
            System.out.println("\n Updated Stock Levels After Transaction:");
            for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
                String productId = entry.getKey();
                Product product = productService.getProductById(productId);
                System.out.println("ID: " + product.getId() + " | New Stock Level: " + product.getStock());
            }
        } catch (Exception e) {
            System.err.println("Transaction failed: " + e.getMessage());
        }
    }


    //@PostConstruct   //runs this method for testing after project is constructed
    public void testAll() {
        //addProductAndPrint(); // call the testing add product method after application is initialised
        //updateStockAndPrint(); //call the testing update stock method
        //createTransactionTest(); //call the testing creating transaction method
    }
}

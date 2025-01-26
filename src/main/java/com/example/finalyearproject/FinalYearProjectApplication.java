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
import java.util.List;


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
    public void createTransaction() {

        //test transaction with products A1, A2, and B1
        List<String> productIds = Arrays.asList("A1", "A2", "B1");
        double paymentReceived = 10.00;

        //create the transaction
        Transaction transaction = transactionService.createTransaction(productIds, paymentReceived);

        //print out transaction details
        System.out.println("Transaction created:");
        System.out.println("Total Cost: £" + String.format("%.2f", transaction.getTotalCost()));
        System.out.println("Payment Received: £" + String.format("%.2f", transaction.getPaymentReceived()));
        System.out.println("Change Given: £" + String.format("%.2f", transaction.getChangeGiven()));

        //print out updated stock levels
        System.out.println("Updated stock levels after transaction:");
        for (String id : productIds) {
            Product product = productService.getProductById(id);
            System.out.println("ID: " + product.getId() + ", Stock Level: " + product.getStock());
        }
    }

    //@PostConstruct   //runs this method for testing after project is constructed
    public void testAll() {
        addProductAndPrint(); // call the testing add product method after application is initialised
        updateStockAndPrint(); //call the testing update stock method
        createTransaction(); //call the testing creating transaction method
    }
}

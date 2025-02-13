package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.repository.ProductRepo;
import com.example.finalyearproject.repository.TransactionRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private ProductService productService;

    //method to create a transaction
    @Transactional
    public Transaction createTransaction(Map<String, Integer> productQuantities, double paymentReceived) {
        List<Product> products = fetchProducts(productQuantities); //list of products

        //call method to calculate total cost based on products and their quantities bought
        double totalCost = calculateTotalCost(productQuantities);


        validatePayment(totalCost, paymentReceived);  //validate payment first
        deductStock(productQuantities);   //deduct stock using the quantities of each product purchased
        return saveTransaction(products, totalCost, paymentReceived, productQuantities); //save transaction into DB
    }

    //method to fetch a transaction from the DB by its ID
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }


    //fetches the products based on the list of product ids and quantities
    private List<Product> fetchProducts(Map<String, Integer> productQuantities) {
        List<Product> products = productRepo.findAllById(productQuantities.keySet());

        for (Product product : products) {
            int requestedQuantity = productQuantities.get(product.getId());

            if (product.getStock() < requestedQuantity) {  //if not enough stock for chosen quantity of products
                throw new RuntimeException("Not enough stock for " + product.getName() + ". Available: " + product.getStock());
            }
        }
        return products;
    }

    //method to calculate total cost of all products
    private double calculateTotalCost(Map<String, Integer> productQuantities) {
        return
                productQuantities.entrySet().stream()
                        .mapToDouble(entry -> {
                            Product product = productRepo.findById(entry.getKey()).orElseThrow();
                            return product.getPrice() * entry.getValue();
                        })
                        .sum();  //return sum of all product prices multiplied by the quantity bought, for total price
    }

    //method to validate payment
    private void validatePayment(double totalCost, double paymentReceived) {
        if (paymentReceived < totalCost) { //validates payment by checking if the total cost exceeds the payment received
            throw new RuntimeException("Insufficient payment. Total cost: " + totalCost);
            //if so throw an exception
        }
    }

    //method to deduct stock using updateStock method from ProductService
    private void deductStock(Map<String, Integer> productQuantities) {
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            productService.updateStock(entry.getKey(), -entry.getValue());
            System.out.println("Deducted "+entry.getValue()+" of stock for "+entry.getKey());
        }
    }


    //method to save transaction into DB
    private Transaction saveTransaction(List<Product> products, double totalCost, double paymentReceived, Map<String, Integer> productQuantities) {

        //round all money values before saving to DB to prevent floating point errors
        double roundedTotalCost = roundTwoDP(totalCost);
        double roundedPaymentReceived = roundTwoDP(paymentReceived);
        double roundedChangeGiven = roundTwoDP(roundedPaymentReceived - roundedTotalCost); // Correct rounding

        Transaction transaction = new Transaction(roundedTotalCost, roundedPaymentReceived, roundedChangeGiven, products);

        //save the transaction to repository
        transaction = transactionRepo.save(transaction);
        System.out.println("New Transaction "+transaction+" saved.");

        return transaction;
    }

    //method to create a receipt for a transaction
    public String generateReceipt(Long transactionId) {
        //fetch the transaction from DB by the ID
        Transaction transaction = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        //formatting date and time
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss"); //capital HH specifies 24 hour time
        String formattedDate = dateFormatter.format(transaction.getTransactionDate());
        String formattedTime = timeFormatter.format(transaction.getTransactionDate());


        //building receipt and displaying id, date and time
        StringBuilder receipt = new StringBuilder();  //use StringBuilder to format the receipt
        receipt.append("===== RECEIPT =====\n");
        receipt.append("Transaction ID: ").append(transaction.getId()).append("\n\n");
        receipt.append("Date: ").append(formattedDate).append("\n");
        receipt.append("Time: ").append(formattedTime).append("\n\n");


        //displaying products
        receipt.append("Purchased Products:\n");

        for (Product product : transaction.getProducts()) { //listing all products in the transaction
            receipt.append("- ").append(product.getName())
                    .append(" (£").append(String.format("%.2f", product.getPrice())).append(")\n");
        }

        // displaying the cost, payment and change
        receipt.append("\nTotal Cost: £").append(String.format("%.2f", transaction.getTotalCost())).append("\n");
        receipt.append("Payment Received: £").append(String.format("%.2f", transaction.getPaymentReceived())).append("\n");
        receipt.append("Change Given: £").append(String.format("%.2f", transaction.getChangeGiven())).append("\n");
        receipt.append("===================\n");

        return receipt.toString();
    }

    public double roundTwoDP(double value) {
        DecimalFormat df = new DecimalFormat("#.##"); //ensures only 2 decimal places
        return Double.parseDouble(df.format(value));  //formats and converts back to double
    }

}

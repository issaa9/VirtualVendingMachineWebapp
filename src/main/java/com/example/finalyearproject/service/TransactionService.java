package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.repository.ProductRepo;
import com.example.finalyearproject.repository.TransactionRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

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
    public Transaction createTransaction(List<String> productIds, double paymentReceived) {
        List<Product> products = fetchProducts(productIds);
        double totalCost = calculateTotalCost(products);
        validatePayment(totalCost, paymentReceived);
        deductStock(products);
        return saveTransaction(products, totalCost, paymentReceived);
    }

    //fetches the products based on the list of product ids
    private List<Product> fetchProducts(List<String> productIds) {
        List<Product> products = productRepo.findAllById(productIds);
        for (Product product : products) {
            if (product.getStock() <= 0) {
                throw new RuntimeException("Product out of stock: " + product.getName());
            }
        }
        return products;
    }

    //method to calculate total cost of all products
    private double calculateTotalCost(List<Product> products) {
        return products.stream().mapToDouble(Product::getPrice).sum();
        //converts product list to  a stream then maps the products into their prices then totals up the prices
    }

    //method to validate payment
    private void validatePayment(double totalCost, double paymentReceived) {
        if (paymentReceived < totalCost) { //validates payment by checking if the total cost exceeds the payment received
            throw new RuntimeException("Insufficient payment. Total cost: " + totalCost);
            //if so throw an exception
        }
    }

    //method to deduct stock using updateStock method from ProductService
    private void deductStock(List<Product> products) {
        for (Product product : products) {
            productService.updateStock(product.getId(), -1); //pass -1 to reduce stock by 1
        }
    }


    //method to save transaction into DB
    private Transaction saveTransaction(List<Product> products, double totalCost, double paymentReceived) {
        Transaction transaction = new Transaction(totalCost,paymentReceived,paymentReceived-totalCost,
        products);
        return transactionRepo.save(transaction);
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

}

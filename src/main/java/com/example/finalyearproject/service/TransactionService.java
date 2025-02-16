package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.model.TransactionProduct;
import com.example.finalyearproject.repository.ProductRepo;
import com.example.finalyearproject.repository.TransactionProductRepo;
import com.example.finalyearproject.repository.TransactionRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
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

    @Autowired
    private TransactionProductRepo transactionProductRepo;

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
        Transaction transaction = transactionRepo.findById(transactionId).orElse(null);

        if (transaction == null) {
            System.out.println("ERROR: Transaction with ID " + transactionId + " not found in database!"); //logging
        } else {
            System.out.println("Transaction found: " + transaction); //logging
        }

        return transaction;
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
        double roundedChangeGiven = roundTwoDP(roundedPaymentReceived - roundedTotalCost); //correct rounding

        //create new transaction
        Transaction transaction = new Transaction();
        transaction.setTotalCost(roundedTotalCost);  //set the values
        transaction.setPaymentReceived(roundedPaymentReceived);
        transaction.setChangeGiven(roundedChangeGiven);
        transaction.setTransactionDate(new Date());

        //save the transaction first to generate its ID (needed for transaction_products)
        transactionRepo.save(transaction);

        //create the transaction-product list
        List<TransactionProduct> transactionProducts = new ArrayList<>();

        for (Product product : products) {
            String productId = product.getId();
            int quantity = productQuantities.getOrDefault(productId, 1); //retrieve quantities, default is 1 incase missing

            //create transaction-product entry
            TransactionProduct transactionProduct = new TransactionProduct(transaction.getId(), productId, quantity);
            transactionProducts.add(transactionProduct);
        }

        //save all transaction-product entries to database
        transactionProductRepo.saveAll(transactionProducts);

        //set transactionproducts attribute for transaction entity
        transaction.setTransactionProducts(transactionProducts);

        //print transaction for easy viewing and logging
        System.out.println("New Transaction " + transaction.getId() + " saved with " + transactionProducts.size() + " product entries.");

        return transaction;
    }

    public double roundTwoDP(double value) {
        DecimalFormat df = new DecimalFormat("#.##"); //ensures only 2 decimal places
        return Double.parseDouble(df.format(value));  //formats and converts back to double
    }

}

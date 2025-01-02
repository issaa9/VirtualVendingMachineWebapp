package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.repository.ProductRepo;
import com.example.finalyearproject.repository.TransactionRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (paymentReceived < totalCost) { //validates payment by checking the payment recieved exceeds the total cost
            throw new RuntimeException("Insufficient payment. Total cost: " + totalCost);
            //else it throws an exception
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
        Transaction transaction = new Transaction();
        transaction.setProducts(products);
        transaction.setTotalCost(totalCost);
        transaction.setPaymentReceived(paymentReceived);
        transaction.setChangeGiven(paymentReceived - totalCost);
        return transactionRepo.save(transaction);
    }
}

package com.example.finalyearproject;

import com.example.finalyearproject.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.finalyearproject.service.ProductService;


@SpringBootApplication
public class FinalYearProjectApplication {

    @Autowired
    private ProductService productService;

    public static void main(String[] args) {

        SpringApplication.run(FinalYearProjectApplication.class, args);
    }

    public void addProductAndPrint() {
        // call the addProduct method from ProductService to test that it works
        Product product = productService.addProduct("Test Product", 15.99, 50);

        // print out the product details  to ensure they are stored correctly
        System.out.println("Product added: ");
        System.out.println("Name: " + product.getName());
        System.out.println("Price: " + product.getPrice());
        System.out.println("Stock Level: " + product.getStockLevel());
    }

    @PostConstruct
    public void testAddProduct() {
        addProductAndPrint(); // call the testing method after application is initialised
    }
}

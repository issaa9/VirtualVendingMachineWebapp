package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {        //service class for product table

    @Autowired
    private ProductRepo productRepository;

    // Method to add a product
    public Product addProduct(String name, double price, int stockLevel) {
        Product product = new Product(name, price, stockLevel);
        productRepository.save(product);  // Save product to database
        return product;
    }
}

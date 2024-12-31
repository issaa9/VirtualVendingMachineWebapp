package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {        //service class for Product

    @Autowired
    private ProductRepo productRepository;

    // Method to add a product

    public Product getProductById(String id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);  // return the product if found, or null if not found
    }
    public Product addProduct(String id, String name, double price, int stockLevel) {
        Product product = new Product(id, name, price, stockLevel);
        productRepository.save(product);  // Save product to database
        return product;
    }
}

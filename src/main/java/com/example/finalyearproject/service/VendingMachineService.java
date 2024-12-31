package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VendingMachineService {

    private final ProductRepo productRepository;

    @Autowired
    public VendingMachineService(ProductRepo productRepository) {
        this.productRepository = productRepository;
    }

    public boolean updateStock(String id, int quantity) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            int updatedStock = product.getStock() + quantity;

            // makes sure stock does not go negative
            if (updatedStock < 0) {
                throw new IllegalArgumentException("Insufficient stock to complete the purchase.");
            }

            product.setStock(updatedStock);
            productRepository.save(product); // saves updated stock to the database
            return true;
        }
        return false; // in case product isn't found
    }


}
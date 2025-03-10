package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {        //service class for Product

    @Autowired
    private ProductRepo productRepository;

    //method to retrieve a product by its id
    public Product getProductById(String id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);  // return the product if found, or null if not found
    }

    //method to return a list of all products
    public List<Product> getAllProducts() {
        return productRepository.findAll(); //fetches all products from the database
    }

    //method to add a product to the DB
    public Product addProduct(String id, String name,String category, double price, int stockLevel) {
        Product product = new Product(id, name,category, price, stockLevel);
        productRepository.save(product);  //save product to database
        return product;
    }

    //method to update stock of a product
    public void updateStock(String id, int quantity) {  //method to update stock of product with given id by the quantity given
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {  //use built in Optional container object for better handling in case product is not found
            Product product = optionalProduct.get();
            int updatedStock = product.getStock() + quantity;

            // makes sure stock does not go negative
            if (updatedStock < 0) {
                throw new IllegalArgumentException("Insufficient stock to complete the purchase.");
            }

            product.setStock(updatedStock);
            productRepository.save(product); // saves updated stock to the database
        }
    }
}

package com.example.finalyearproject.controller;


import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private ProductService productService;

    @GetMapping("/main")
    public String showMainPage(Model model) {
        List<Product> products = productService.getAllProducts();  //fetch all products from DB
        model.addAttribute("products", products);  //add products to model attribute
        return "mainpage";
    }


    //controller method for handling AJAX request to fetch products by their IDs
    @GetMapping("/api/cart/getProduct/{id}")  //define API endpoint to allow fetching items by their ID
    @ResponseBody  //allows JSON response instead of rendering a HTML view
    public ResponseEntity<?> getProductById(@PathVariable String id) { //extracts th ID from the URL to be used as a variable
        Optional<Product> product = Optional.ofNullable(productService.getProductById(id)); //fetches product details from DB

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());  //return product data as JSON
        }
        else {
            return ResponseEntity.badRequest().body("Product not found or invalid code."); //error handling
        }
    }

    @GetMapping("/api/cart/checkStock/{id}") //define API endpoint to fetch an item's stock by its ID
    @ResponseBody  //allow JSON response
    public ResponseEntity<?> checkProductStock(@PathVariable String id) {
        Optional<Product> product = Optional.ofNullable(productService.getProductById(id));

        if (product.isPresent()) {  //check if product exists
            int stock = product.get().getStock();  //call getter method for stock attribute
            return ResponseEntity.ok(stock);  //return product stock as a JSON response
        } else {
            return ResponseEntity.badRequest().body("Product not found"); //else return an error message
        }
    }

}


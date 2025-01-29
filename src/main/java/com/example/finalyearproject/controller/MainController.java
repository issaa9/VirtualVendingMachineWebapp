package com.example.finalyearproject.controller;


import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
}


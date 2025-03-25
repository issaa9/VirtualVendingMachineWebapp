package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//controller for the admin dashboard and all admin controls
@Controller
public class AdminController {

    @Autowired
    private ProductService productService;

    @GetMapping("/admin/dashboard") //create endpoint for admin dashboard
    public String showAdminDashboard(Model model) {
        List<Product> products = productService.getAllProducts(); //retrieve all products
        model.addAttribute("products", products);  //add in products as model attribute
        return "admindashboard"; //render the admin dashboard page
    }
}

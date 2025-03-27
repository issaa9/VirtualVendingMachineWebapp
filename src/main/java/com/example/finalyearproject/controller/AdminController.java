package com.example.finalyearproject.controller;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

//controller for the admin dashboard and all admin controls
@Controller
public class AdminController {

    @Autowired
    private ProductService productService;

    //controller method to render the page
    @GetMapping("/admin/dashboard") //create endpoint for admin dashboard
    public String showAdminDashboard(Model model) {
        List<Product> products = productService.getAllProducts(); //retrieve all products
        model.addAttribute("products", products);  //add in products as model attribute
        return "admindashboard"; //render the admin dashboard page
    }

    //controller method to fetch a list of all products
    @GetMapping("/admin/products") //create the endpoint
    @ResponseBody
    public List<Product> getProducts() {

        return productService.getAllProducts(); //retrieve all products from the repository
    }

    //controller method to handle updating stock
    @PostMapping("/admin/update-stock") //create endpoint for updating stock
    @ResponseBody
    public String updateStock(@RequestBody List<Product> updatedProducts) {  //retrieve list of updated products
        for (Product p : updatedProducts) {  //for every updated product
            productService.setNewStockById(p.getId(), p.getStock());   //set the new stock value
        }
        return "Stock updated successfully"; //return success message
    }


}

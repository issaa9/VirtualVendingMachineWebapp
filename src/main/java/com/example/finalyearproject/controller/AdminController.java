package com.example.finalyearproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//controller for the admin dashboard and all admin controls
@Controller
public class AdminController {


    @GetMapping("/admin/dashboard") //create endpoint for admin dashboard
    public String showAdminDashboard() {

        return "admindashboard"; //render the admin dashboard page
    }
}

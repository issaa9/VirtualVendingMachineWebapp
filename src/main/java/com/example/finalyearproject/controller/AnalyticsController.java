package com.example.finalyearproject.controller;

import com.example.finalyearproject.dto.AnalyticsSummaryDTO;
import com.example.finalyearproject.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;




//controller for the analytics page
@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public String showAnalyticsPage() {
        return "analytics"; //render the page
    }

    @ResponseBody
    @GetMapping("/summary")  //endpoint for fetching summary data
    public AnalyticsSummaryDTO getSummaryData(@AuthenticationPrincipal UserDetails userDetails) {
        return analyticsService.getUserSummary(userDetails.getUsername());  //call the service method to get the user's summary data
    }

}

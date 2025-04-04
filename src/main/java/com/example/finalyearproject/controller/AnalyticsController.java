package com.example.finalyearproject.controller;

import com.example.finalyearproject.dto.AnalyticsSummaryDTO;
import com.example.finalyearproject.dto.PurchaseFrequencyDTO;
import com.example.finalyearproject.dto.SpendingTrendDTO;
import com.example.finalyearproject.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


//controller for the analytics page
@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    //controller method for showing the page
    @GetMapping
    public String showAnalyticsPage() {

        return "analytics"; //render the page
    }

    //controller method for analytics summary
    @ResponseBody
    @GetMapping("/summary")  //endpoint for fetching summary data
    public AnalyticsSummaryDTO getSummaryData(@AuthenticationPrincipal UserDetails userDetails) {
        return analyticsService.getUserSummary(userDetails.getUsername());  //call the service method to get the user's summary data
    }

    //controller method for purchase frequency
    @ResponseBody
    @GetMapping("/frequency") //endpoint for fetching purchase frequency data
    public List<PurchaseFrequencyDTO> getPurchaseFrequency(@AuthenticationPrincipal UserDetails userDetails) {
        return analyticsService.getUserPurchaseFrequency(userDetails.getUsername()); //call the service method (passing in username) to fetch the dto data and return it to frontend
    }

    //controller method for spending trend
    @ResponseBody
    @GetMapping("/spending")  //endpoint for fetching spending trend data
    public List<SpendingTrendDTO> getSpendingTrend(@AuthenticationPrincipal UserDetails userDetails) {
        return analyticsService.getMonthlySpendingTrend(userDetails.getUsername());
    }


}

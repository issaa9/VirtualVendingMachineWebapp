package com.example.finalyearproject.service;

import com.example.finalyearproject.dto.AnalyticsSummaryDTO;
import com.example.finalyearproject.dto.PurchaseFrequencyDTO;
import com.example.finalyearproject.dto.SpendingTrendDTO;
import com.example.finalyearproject.dto.TopProductQuantityDTO;
import com.example.finalyearproject.repository.TransactionProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finalyearproject.repository.TransactionRepo;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private TransactionProductRepo transactionProductRepo;



    public AnalyticsSummaryDTO getUserSummary(String username) {

        //create new empty dto object
        AnalyticsSummaryDTO dto = new AnalyticsSummaryDTO();

        //retrieve the summary data by calling the repository methods (queries)
        int totalPurchases = transactionRepo.countByUser(username);
        Double totalSpent = transactionRepo.sumTotalSpentByUser(username);
        String mostActiveDay = transactionRepo.findMostActiveDay(username);
        int uniqueItemsPurchased = transactionProductRepo.countDistinctProductsPurchasedByUser(username);

        //set the attributes for the dto, using the retrieved summary data
        dto.setTotalPurchases(totalPurchases);
        dto.setTotalSpent(totalSpent != null ? totalSpent : 0.00); //validate to 0 if total spent is null
        dto.setMostActiveDay(mostActiveDay != null ? mostActiveDay : "N/A");  //validate to N/A if no most active day
        dto.setUniqueItemsPurchased(uniqueItemsPurchased);

        return dto; //return the dto object containing the summary data
    }

    //method to retrieve the purchase frequency data from the repository
    public List<PurchaseFrequencyDTO> getUserPurchaseFrequency(String username) {
        return transactionRepo.findPurchaseFrequencyByUser(username); //return the dto from the repository

    }

    //method to retrieve the monthly spending trend from the repository
    public List<SpendingTrendDTO> getMonthlySpendingTrend(String username) {
        return transactionRepo.findSpendingTrendByUser(username);
    }

    //method to retrieve item breakdown data from repository
    public List<TopProductQuantityDTO> getTopProductQuantities(String username) {
        return transactionRepo.findProductQuantitiesByUser(username);
    }



}

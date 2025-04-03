package com.example.finalyearproject.service;

import com.example.finalyearproject.dto.AnalyticsSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finalyearproject.repository.TransactionRepo;

@Service
public class AnalyticsService {

    @Autowired
    private TransactionRepo transactionRepo;

    public AnalyticsSummaryDTO getUserSummary(String username) {

        //retrieve the summary data by calling the repository methods (queries)
        int totalPurchases = transactionRepo.countByUser(username);
        double totalSpent = transactionRepo.sumTotalSpentByUser(username);
        String mostActiveDay = transactionRepo.findMostActiveDay(username);

        return new AnalyticsSummaryDTO(totalPurchases, totalSpent, mostActiveDay); //returns a dto object containing the summary data
    }
}

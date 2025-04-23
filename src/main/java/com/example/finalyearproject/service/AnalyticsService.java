package com.example.finalyearproject.service;

import com.example.finalyearproject.dto.AnalyticsSummaryDTO;
import com.example.finalyearproject.dto.PurchaseFrequencyDTO;
import com.example.finalyearproject.dto.SpendingTrendDTO;
import com.example.finalyearproject.dto.TopProductQuantityDTO;
import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.model.TransactionProduct;
import com.example.finalyearproject.repository.ProductRepo;
import com.example.finalyearproject.repository.TransactionProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finalyearproject.repository.TransactionRepo;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private TransactionProductRepo transactionProductRepo;

    @Autowired
    private ProductRepo productRepo;



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

    //method to retrieve and structure smart recommendation insight data
    public Map<String, Object> generateSmartInsights(String username) {
        Map<String, Object> result = new HashMap<>();  //initialise the hashmap to store all required data

        List<Transaction> transactions = transactionRepo.findByUser(username); //retrieve all transactions for the user

        //Top Category
        Map<String, Integer> categoryCount = new HashMap<>(); //initialise hashmap for counting quantities per category
        for (Transaction t : transactions) {
            for (TransactionProduct tp : t.getTransactionProducts()) {
                productRepo.findById(tp.getProductId()).ifPresent(p ->
                        categoryCount.merge(p.getCategory(), tp.getQuantity(), Integer::sum)); //sum up the quantity of each product in each category and store the summed quantity for each category in the hashmap
            }
        }
        //find the top category by finding the category with the max value of quantities
        String topCategory = categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");
        result.put("topCategory", topCategory); //store top category

        //Average Spend
        double avgSpend = transactions.stream().mapToDouble(Transaction::getTotalCost).average().orElse(0.0); //get average total cost (or fallback to 0)
        result.put("avgSpend", avgSpend); //store average spend

        //Collaborative Strength
        int productCount = transactions.stream().mapToInt(t -> t.getTransactionProducts().size()).sum(); //find how many purchases the user has made
        String collabStrength = productCount < 3 ? "Strong" : productCount < 6 ? "Moderate" : "Low"; //low purchases means higher dependence on collab score for recommendations so high collab strength, opposite means low collab strength
        result.put("collabStrength", collabStrength); //store collab strength

        //Recommended items
        List<Product> allProducts = productRepo.findAll(); //get a list of all products

        //get top 3 items in the top category
        List<String> categoryItems = allProducts.stream()
                .filter(p -> topCategory.equalsIgnoreCase(p.getCategory()))
                .map(p -> p.getId() + " - " + p.getName()) //get ID and name
                .limit(3)
                .toList();

        //get top 3 items with cost closest to average spend
        List<String> priceItems = allProducts.stream()
                .filter(p -> Math.abs(p.getPrice() - avgSpend) >= 0.0)
                .sorted(Comparator.comparingDouble(p -> Math.abs(p.getPrice() - avgSpend)))
                .map(p -> p.getId() + " - " + p.getName()) //get ID and name
                .limit(3)
                .toList();

        //get top 3 items collaboratively (from other users)
        List<String> collabItems = transactionRepo.findPopularProductsNotInUser(username, List.of()).stream()
                .map(id -> productRepo.findById(id)
                        .map(p -> p.getId() + " - " + p.getName()) //get ID and name
                        .orElse(id)) // fallback to just ID if product not found (rare case)
                .limit(3)
                .toList();


        //store the top 3 product lists in result
        result.put("categoryItems", categoryItems);
        result.put("priceItems", priceItems);
        result.put("collabItems", collabItems);

        return result; //return the result hashmap
    }




}

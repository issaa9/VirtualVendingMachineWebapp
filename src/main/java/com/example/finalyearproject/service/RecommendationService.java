package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.model.TransactionProduct;
import com.example.finalyearproject.repository.ProductRepo;
import com.example.finalyearproject.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private ProductRepo productRepo;

    //method to smartly generate a list of recommended products and return it to the controller
    //uses an algorithm containing ML concepts for advanced and personalised recommendations
    public List<String> generateRecommendations(String username) {

        //retrieve a list of top products already purchased by the user, using the repo method
        List<String> alreadyPurchased = transactionRepo.findTopProductsByUser(username);

        //1.Score all categories based on user preference
        Map<String, Integer> categoryScore = new HashMap<>(); //initialise a hashmap for scoring categories
        List<Transaction> transactions = transactionRepo.findByUser(username); //retrieve all the user's transactions

        //iterate for every user transaction
        for (Transaction t : transactions) {
            for (TransactionProduct tp : t.getTransactionProducts()) { //iterate for every product in the transaction
                //attempt to find the product, if found total up the quantity of each category
                productRepo.findById(tp.getProductId()).ifPresent(p -> categoryScore.merge(p.getCategory(), tp.getQuantity(), Integer::sum));
            }
        }

        //2.Score all products by match to user's preferences
        List<Product> allProducts = productRepo.findAll(); //get a list of all products
        Map<String, Double> productScores = new HashMap<>(); //create new hashmap for scoring products

        for (Product p : allProducts) { //iterate for all products
            if (alreadyPurchased.contains(p.getId())) continue; //if user has already purchased the product, skip it

            double score = 0; //initialise a score variable

            //category-based scoring
            score += categoryScore.getOrDefault(p.getCategory(), 0); //boost the score based on how often the user has already bought from this product's category

            //price range similarity (bonus scoring)
            score += 1.0 / (1 + Math.abs(avgSpent(transactions) - p.getPrice())); //gives a bonus increase to the score based on how similar the price of the product is to the user's average total spent

            productScores.put(p.getId(), score); //store the productID and its corresponding score in the hashmap
        }

        //3.Collaborative filtering scoring boosts: higher recommendation for items bought by similar users

        //retrieve a list of top products bought by other users (exclude the current user and products they have already bought), using the repo method
        List<String> collabItems = transactionRepo.findPopularProductsNotInUser(username, alreadyPurchased);

        for (String id : collabItems) { //iterate for each collaborative suggested item
            productScores.put(id, productScores.getOrDefault(id, 0.0) + 3.0); //boost their scores by 3
        }

        //4.Create a final list of the top scoring items and return it

        return productScores.entrySet().stream() //convert productScores hashmap to a stream
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) //sort by score (descending order)
                .limit(5) //select the top 5
                .map(Map.Entry::getKey) //retrieve only the IDs
                .toList(); //convert to a list
    }

    //helper method to calculate the average amount spent by the user
    private double avgSpent(List<Transaction> transactions) {
        return transactions.stream().mapToDouble(Transaction::getTotalCost).average().orElse(0); //maps the transactions list to the average of all of it's total costs (fallback to 0)
    }
}


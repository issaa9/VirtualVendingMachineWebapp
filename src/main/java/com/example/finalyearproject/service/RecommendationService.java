package com.example.finalyearproject.service;

import com.example.finalyearproject.dto.RecommendationDTO;
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
    public List<RecommendationDTO> generateRecommendations(String username) {

        //retrieve a list of top products already purchased by the user, using the repo method
        List<String> alreadyPurchased = transactionRepo.findTopProductsByUser(username);

        //1.Score all categories based on user preference
        Map<String, Integer> categoryScore = new HashMap<>(); //initialise a hashmap for scoring categories
        List<Transaction> transactions = transactionRepo.findByUser(username); //retrieve all the user's transactions

        //iterate for every user transaction
        for (Transaction t : transactions) {
            for (TransactionProduct tp : t.getTransactionProducts()) { //iterate for every product in the transaction
                //attempt to find the product, if found total up the quantity of each category
                productRepo.findById(tp.getProductId()).ifPresent(p ->
                        categoryScore.merge(p.getCategory(), tp.getQuantity(), Integer::sum));
            }
        }

        //2.Score all products by match to user's preferences
        List<Product> allProducts = productRepo.findAll(); //get a list of all products
        Map<String, Double> productScores = new HashMap<>(); //create new hashmap for scoring products
        Map<String, Double> categoryContrib = new HashMap<>(); //hashmap to track category-based contribution
        Map<String, Double> priceContrib = new HashMap<>(); //hashmap to track price-based contribution
        Map<String, Double> collabContrib = new HashMap<>(); //hashmap to track collaborative contribution

        double avgSpend = avgSpent(transactions); //get the user's average spend
        boolean isNewUser = transactions.isEmpty(); //check if user is new (has no transactions)

        int totalCategoryQuantity = categoryScore.values().stream().mapToInt(Integer::intValue).sum(); //get total number of categories
        for (Product p : allProducts) { //iterate for all products
            if (alreadyPurchased.contains(p.getId())) continue; //if user has already purchased the product, skip it

            double score = 0; //initialise a total score variable

            //initialise partial scores for category and price
            double catBoost = 0;
            double priceBoost = 0;

            //category-based scoring
            int catScore = categoryScore.getOrDefault(p.getCategory(), 0); //get existing value or default to 0
            if (catScore > 0) {
                double raw = (double) catScore / totalCategoryQuantity; //get a raw category score between 0-1
                catBoost = Math.log1p(catScore) * raw; //scale category boost down using log function
                score += catBoost; //increment total score with category boost
            }


            //price range similarity
            if (!isNewUser) { //only consider price match for experienced users
                double priceDiff = Math.abs(avgSpend - p.getPrice()); //calculate difference between product price and user average spend
                    priceBoost = 1.0 / (1 + priceDiff) * 1.4; //set the price score boost based on closeness to average price (now with a 1.4 multiplier to give it more chance)
                    score += priceBoost;  //increment the total score with the price score boost
            }

            //store individual contributions for comparison later
            if (catBoost > 0) categoryContrib.put(p.getId(), catBoost);
            if (priceBoost > 0) priceContrib.put(p.getId(), priceBoost);

            productScores.put(p.getId(), score); //store the productID and its corresponding score in the hashmap
        }

        //3.Collaborative filtering scoring boosts: higher recommendation for items bought by similar users

        //retrieve a list of top products bought by other users (exclude the current user and products they have already bought), using the repo method
        List<String> collabItems = transactionRepo.findPopularProductsNotInUser(username, alreadyPurchased);

        for (String id : collabItems) { //iterate for each collaborative suggested item
            double boost = setAdaptiveCollabBoost(transactions); //define the collaborative boost value from the helper method
            double updatedScore = productScores.getOrDefault(id, 0.0) + boost; //get the existing score (default to 0 if none) and add on the collaborative score boost
            productScores.put(id, updatedScore); //boost their scores
            collabContrib.put(id, boost); //track the collab contribution
        }

        //4.Create a final list of the top scoring items and return it with reasons based on contribution levels

        return productScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) //sort by score (descending order)
                .limit(5) //select the top 5
                .map(entry -> {
                    String id = entry.getKey(); //get the id
                    double total = entry.getValue(); //get the total score

                    //fetch the contributions (default to 0 if none)
                    double cat = categoryContrib.getOrDefault(id, 0.0);
                    double price = priceContrib.getOrDefault(id, 0.0);
                    double collab = collabContrib.getOrDefault(id, 0.0);

                    String reason = "hybrid"; //fallback/default reason

                    //calculate contribution percentages
                    if (total > 0) {
                        double catPct = cat / total;
                        double pricePct = price / total;
                        double collabPct = collab / total;

                        if (catPct >= pricePct && catPct >= collabPct) {
                            reason = "category"; //set the reason if category percent is dominant
                        } else if (pricePct >= catPct && pricePct >= collabPct) {
                            reason = "price";  //set the reason if price percent is dominant
                        } else {
                            reason = "collab";  //set the reason as collab in all other cases
                        }
                    }

                    System.out.printf("Product %s => score %.2f (cat %.2f, price %.2f, collab %.2f) -> reason: %s%n",
                            id, total, cat, price, collab, reason); //log all scoring and reason data for testing

                    return new RecommendationDTO(id, reason); //map to RecommendationDTO including reason
                })
                .toList(); //return as a list
    }


    //helper method to calculate the average amount spent by the user
    private double avgSpent(List<Transaction> transactions) {
        return transactions.stream().mapToDouble(Transaction::getTotalCost).average().orElse(0); //maps the transactions list to the average of all of it's total costs (fallback to 0)
    }

    //helper method to set a boost for the collab scoring, dependent on the user's transaction history
    private double setAdaptiveCollabBoost(List<Transaction> transactions) {
        //count the number of products in the users transactions
        int productCount = 0;
        for (Transaction t : transactions) {
            productCount += t.getTransactionProducts().size();
        }

        if (productCount < 3) return 3.0;       //for poor transaction history set boost high (ensure collab is more important)
        if (productCount < 6) return 2.0;       //for mid transaction history set mid boost
        return 0.9;                             //rich transaction history so low boost (depend more on user based features rather than collab)
    }

}


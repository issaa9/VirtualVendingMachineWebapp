package com.example.finalyearproject.repository;

import com.example.finalyearproject.dto.PurchaseFrequencyDTO;
import com.example.finalyearproject.dto.SpendingTrendDTO;
import com.example.finalyearproject.dto.TopProductQuantityDTO;
import com.example.finalyearproject.model.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;



public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("SELECT SUM(t.totalCost) FROM Transaction t")
    Double sumAllTransactions();

    List<Transaction> findByUser(String username);  //allow filtering transactions by username

    @Query(value = "SELECT * FROM transactions t " +    //defining a custom SQL query for filtering transactions in the DB
            "WHERE (:transactionId IS NULL OR t.id = :transactionId) " +
            "AND (:username IS NULL OR t.user = :username) " +
            "AND (:startDate IS NULL OR t.transaction_date >= :startDate) " +
            "AND (:endDate IS NULL OR t.transaction_date <= :endDate) " +
            "AND (:minTotalCost IS NULL OR t.total_cost >= :minTotalCost) " + //set the condition to include all possible filters, but allow them to be ignored if the parameters are null
            "AND (:maxTotalCost IS NULL OR t.total_cost <= :maxTotalCost) " +
            "AND (:minPayment IS NULL OR t.payment_received >= :minPayment) " +
            "AND (:maxPayment IS NULL OR t.payment_received <= :maxPayment) " +
            "AND (:minChange IS NULL OR t.change_given >= :minChange) " +
            "AND (:maxChange IS NULL OR t.change_given <= :maxChange)",
            nativeQuery = true)
    List<Transaction> filterTransactions(   //define the repository method
            @Param("transactionId") Long transactionId,
            @Param("username") String username,
            @Param("startDate") LocalDate startDate,  //set all the parameters
            @Param("endDate") LocalDate endDate,
            @Param("minTotalCost") Double minTotalCost,
            @Param("maxTotalCost") Double maxTotalCost,
            @Param("minPayment") Double minPayment,
            @Param("maxPayment") Double maxPayment,
            @Param("minChange") Double minChange,
            @Param("maxChange") Double maxChange
    );

    //ANALYTICS QUERIES


    //queries for the summary data

    //query to count the number of transactions made by the user
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = :username")
    int countByUser(@Param("username") String username); //repository method

    //query to calculate the total amount spent by the user
    @Query("SELECT SUM(t.totalCost) FROM Transaction t WHERE t.user = :username")
    Double sumTotalSpentByUser(@Param("username") String username); //repository method

    //query to work out the most active day (day with most purchases)
    @Query("SELECT FUNCTION('DAYNAME', t.transactionDate) FROM Transaction t WHERE t.user = :username GROUP BY FUNCTION('DAYNAME', t.transactionDate) ORDER BY COUNT(t) DESC LIMIT 1")
    String findMostActiveDay(@Param("username") String username); //repository method



    //query for purchase frequency analytics data which gets the number of transactions per month for a user
    @Query("SELECT new com.example.finalyearproject.dto.PurchaseFrequencyDTO(FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m'), COUNT(t)) " +
            "FROM Transaction t WHERE t.user = :username " +
            "GROUP BY FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')")
    List<PurchaseFrequencyDTO> findPurchaseFrequencyByUser(@Param("username") String username); //repository method


    //query method for spending trend data which finds the total spend per month by username
    @Query("SELECT new com.example.finalyearproject.dto.SpendingTrendDTO(FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m'), SUM(t.totalCost)) " +
            "FROM Transaction t WHERE t.user = :username " +
            "GROUP BY FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')")
    List<SpendingTrendDTO> findSpendingTrendByUser(@Param("username") String username);  //repository method


    //query method for product quantities bought to be used in analytics (item breakdown pie chart)
    @Query("SELECT new com.example.finalyearproject.dto.TopProductQuantityDTO(p.id, p.name, SUM(tp.quantity)) " +
            "FROM TransactionProduct tp " +
            "JOIN Transaction t ON t.id = tp.transactionId " +
            "JOIN Product p ON tp.productId = p.id " +
            "WHERE t.user = :username " +
            "GROUP BY p.id, p.name " +
            "ORDER BY SUM(tp.quantity) DESC")
    List<TopProductQuantityDTO> findProductQuantitiesByUser(@Param("username") String username);





    //query method to find the top 5 products purchased by the user, to be used for Smart Recommendations
    @Query("SELECT tp.productId FROM TransactionProduct tp " +
            "JOIN Transaction t ON t.id = tp.transactionId " +
            "WHERE t.user = :username " +
            "GROUP BY tp.productId " +
            "ORDER BY SUM(tp.quantity) DESC LIMIT 5")
    List<String> findTopProductsByUser(@Param("username") String username);

}

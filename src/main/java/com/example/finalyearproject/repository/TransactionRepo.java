package com.example.finalyearproject.repository;

import com.example.finalyearproject.model.Transaction;

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

    //queries for the summary data

    //query to count the number of transactions made by the user
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = :username")
    int countByUser(@Param("username") String username);

    //query to calculate the total amount spent by the user
    @Query("SELECT SUM(t.totalCost) FROM Transaction t WHERE t.user = :username")
    Double sumTotalSpentByUser(@Param("username") String username);

    //query to work out the most active day (day with most purchases)
    @Query("SELECT FUNCTION('DAYNAME', t.transactionDate) FROM Transaction t WHERE t.user = :username GROUP BY FUNCTION('DAYNAME', t.transactionDate) ORDER BY COUNT(t) DESC LIMIT 1")
    String findMostActiveDay(@Param("username") String username);


}

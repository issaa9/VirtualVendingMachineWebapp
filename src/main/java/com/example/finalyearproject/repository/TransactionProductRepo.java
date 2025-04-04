package com.example.finalyearproject.repository;


import com.example.finalyearproject.model.TransactionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionProductRepo extends JpaRepository<TransactionProduct, Long> {

    //query to count how many unique products the user has purchased
    @Query(value = """
    SELECT COUNT(DISTINCT tp.product_id)
    FROM transactions t
    JOIN transaction_products tp ON t.id = tp.transaction_id
    WHERE t.user = :username
""", nativeQuery = true)
    int countDistinctProductsPurchasedByUser(@Param("username") String username); //repository method

}
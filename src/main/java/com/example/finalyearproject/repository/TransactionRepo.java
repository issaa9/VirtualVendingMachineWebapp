package com.example.finalyearproject.repository;

import com.example.finalyearproject.model.Transaction;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("SELECT SUM(t.totalCost) FROM Transaction t")
    Double sumAllTransactions();

    List<Transaction> findByUser(String username);  //allow filtering transactions by username
}

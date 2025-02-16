package com.example.finalyearproject.repository;

import com.example.finalyearproject.model.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
}

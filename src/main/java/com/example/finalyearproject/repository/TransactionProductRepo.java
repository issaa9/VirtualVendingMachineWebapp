package com.example.finalyearproject.repository;


import com.example.finalyearproject.model.TransactionProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionProductRepo extends JpaRepository<TransactionProduct, Long> {
}
package com.example.finalyearproject.repository;

import com.example.finalyearproject.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepo extends JpaRepository<Product, Long> {
}

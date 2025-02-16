package com.example.finalyearproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transaction_products")
public class TransactionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    private int quantity; //store the quantity of this product in the transaction

    //constructors
    public TransactionProduct() {}

    public TransactionProduct(long transactionId, String productId, int quantity) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
    }

    //getters and Setters
    public Long getId() {

        return id;
    }

    public long getTransactionId() {

        return transactionId;
    }

    public void setTransactionId(long transactionId) {

        this.transactionId = transactionId;
    }

    public String getProductId() {

        return productId;
    }

    public void setProductId(String product) {

        this.productId = productId;
    }

    public int getQuantity() {

        return quantity;
    }

    public void setQuantity(int quantity) {

        this.quantity = quantity;
    }
}


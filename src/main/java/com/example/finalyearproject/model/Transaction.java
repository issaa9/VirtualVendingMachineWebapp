package com.example.finalyearproject.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalCost;

    private double paymentReceived;

    private double changeGiven;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @ManyToMany   //establishes the many to many relation
    @JoinTable(   //join the tables
            name = "transaction_products",  //third table for many to many relation
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")  //join by ids
    )
    private List<Product> products;

    public Transaction() {
        this.transactionDate = new Date();  //default constructor
    }

    public Transaction(double totalCost, double paymentReceived, double changeGiven, List<Product> products) {
        this.totalCost = totalCost;
        this.paymentReceived = paymentReceived;   //constructor
        this.changeGiven = changeGiven;
        this.transactionDate = new Date();
        this.products = products;
    }

    //getters and setters:
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(double paymentReceived) {
        this.paymentReceived = paymentReceived;
    }

    public double getChangeGiven() {
        return changeGiven;
    }

    public void setChangeGiven(double changeGiven) {
        this.changeGiven = changeGiven;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", totalCost=" + totalCost +
                ", paymentReceived=" + paymentReceived +
                ", changeGiven=" + changeGiven +
                ", transactionDate=" + transactionDate +
                ", products=" + products +
                '}';
    }
}

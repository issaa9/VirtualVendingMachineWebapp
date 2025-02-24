package com.example.finalyearproject.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",nullable = false)
    private Long id;
    @Column(name="total_cost",nullable = false)
    private double totalCost;
    @Column(name="payment_received",nullable = false)
    private double paymentReceived;
    @Column(name="change_given",nullable = false)
    private double changeGiven;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="transaction_date",nullable = false)
    private Date transactionDate;

    @OneToMany(mappedBy = "transactionId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionProduct> transactionProducts = new ArrayList<>();

    public Transaction(){

    }

    public Transaction(Long id, double totalCost, double paymentReceived, double changeGiven, Date transactionDate, List<TransactionProduct> transactionProducts) {
        this.id = id;
        this.totalCost = totalCost;
        this.paymentReceived = paymentReceived;
        this.changeGiven = changeGiven;
        this.transactionDate = transactionDate;
        this.transactionProducts = transactionProducts;
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

    public List<TransactionProduct> getTransactionProducts() {
        return transactionProducts;
    }

    public void setTransactionProducts(List<TransactionProduct> transactionProducts) {
        this.transactionProducts = transactionProducts;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", totalCost=" + totalCost +
                ", paymentReceived=" + paymentReceived +
                ", changeGiven=" + changeGiven +
                ", transactionDate=" + transactionDate +
                ", transactionProducts=" + transactionProducts +
                '}';
    }
}

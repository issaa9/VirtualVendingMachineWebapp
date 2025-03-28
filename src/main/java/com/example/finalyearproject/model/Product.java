package com.example.finalyearproject.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name="id",nullable= false)
    private String id;

    @Column(name="name",nullable = false)
    private String name;

    @Column(name="price",nullable = false)
    private double price;

    @Column(name="stock",nullable = false)
    private int stock;

    @Column(name="category",nullable = false)
    private String category;

    @Column(name="auto_stock_enabled")
    private boolean autoStockEnabled;

    @Column(name="stock_threshold")
    private Integer stockThreshold;

    @Column(name="update_amount")
    private Integer updateAmount;

    @Column(name="image_url")
    private String imageUrl; //field to store product image URL

    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionProduct> transactionProducts = new ArrayList<>();

    public Product() {        //default constructor

    }

    public Product(String id, String name, String category, double price, int stock) {   //constructor
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    //getters and setters:
    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public double getPrice() {

        return price;
    }

    public void setPrice(double price) {

        this.price = price;
    }

    public int getStock() {

        return stock;
    }

    public void setStock(int stockLevel) {

        this.stock = stockLevel;
    }

    public List<TransactionProduct> getTransactionProducts() {
        return transactionProducts; }

    public void setTransactionProducts(List<TransactionProduct> transactionProducts) {
        this.transactionProducts = transactionProducts;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAutoStockEnabled() {
        return autoStockEnabled;
    }

    public void setAutoStockEnabled(boolean autoStockEnabled) {
        this.autoStockEnabled = autoStockEnabled;
    }

    public Integer getStockThreshold() {
        return stockThreshold;
    }

    public void setStockThreshold(Integer stockThreshold) {
        this.stockThreshold = stockThreshold;
    }

    public Integer getUpdateAmount() {
        return updateAmount;
    }

    public void setUpdateAmount(Integer updateAmount) {
        this.updateAmount = updateAmount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", imageUrl='" + imageUrl + '\'' +
                ", transactionProducts=" + transactionProducts +
                '}';
    }
}

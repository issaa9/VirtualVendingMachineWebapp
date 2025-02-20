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

    @Column(name="image_url")
    private String imageUrl; // New field to store product image URL

    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionProduct> transactionProducts = new ArrayList<>();

    public Product() {        //default constructor

    }

    public Product(String id, String name, double price, int stock) {   //constructor
        this.id = id;
        this.name = name;
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

    public List<TransactionProduct> getTransactionProducts() { return transactionProducts; }
    public void setTransactionProducts(List<TransactionProduct> transactionProducts) {
        this.transactionProducts = transactionProducts;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

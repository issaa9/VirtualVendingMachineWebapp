package com.example.finalyearproject.model;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "products")
public class Product {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int stock;

    @ManyToMany(mappedBy = "products")   //recognising the many to many relation
    private List<Transaction> transactions;

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


    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockLevel=" + stock +
                '}';
    }
}

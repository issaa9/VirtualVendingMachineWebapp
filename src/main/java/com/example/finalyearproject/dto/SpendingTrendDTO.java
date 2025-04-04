package com.example.finalyearproject.dto;

public class SpendingTrendDTO {
    private String month;
    private double totalSpent;

    public SpendingTrendDTO(Object month, double totalSpent) {
        this.month = String.valueOf(month);
        this.totalSpent = totalSpent;
    }

    public String getMonth() {
        return month;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
}

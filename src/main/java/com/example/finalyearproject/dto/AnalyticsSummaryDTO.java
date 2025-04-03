package com.example.finalyearproject.dto;

//new dto class for combining data into single objects to be used for the analytics page
public class AnalyticsSummaryDTO {

    //attributes
    private int totalPurchases;
    private double totalSpent;
    private String mostActiveDay;

    private int uniqueItemsPurchased;

    //constructors
    public AnalyticsSummaryDTO() {

    }

    public AnalyticsSummaryDTO(int totalPurchases, double totalSpent, String mostActiveDay, int uniqueItemsPurchased) {
        this.totalPurchases = totalPurchases;
        this.totalSpent = totalSpent;
        this.mostActiveDay = mostActiveDay;
        this.uniqueItemsPurchased = uniqueItemsPurchased;
    }

    //getters and setters
    public int getTotalPurchases() {
        return totalPurchases; }
    public void setTotalPurchases(int totalPurchases) {
        this.totalPurchases = totalPurchases; }

    public double getTotalSpent() {
        return totalSpent; }
    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent; }

    public String getMostActiveDay() {
        return mostActiveDay; }
    public void setMostActiveDay(String mostActiveDay) {
        this.mostActiveDay = mostActiveDay; }

    public int getUniqueItemsPurchased() {
        return uniqueItemsPurchased;
    }

    public void setUniqueItemsPurchased(int uniqueItemsPurchased) {
        this.uniqueItemsPurchased = uniqueItemsPurchased;
    }
}

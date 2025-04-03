package com.example.finalyearproject.dto;

//new dto class for combining data into single objects to be used for the analytics page
public class AnalyticsSummaryDTO {

    //attributes
    private int totalPurchases;
    private double totalSpent;
    private String mostActiveDay;

    //constructors
    public AnalyticsSummaryDTO() {}

    public AnalyticsSummaryDTO(int totalPurchases, double totalSpent, String mostActiveDay) {
        this.totalPurchases = totalPurchases;
        this.totalSpent = totalSpent;
        this.mostActiveDay = mostActiveDay;
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
}

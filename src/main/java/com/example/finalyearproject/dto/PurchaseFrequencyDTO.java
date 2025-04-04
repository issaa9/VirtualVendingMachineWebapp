package com.example.finalyearproject.dto;

import java.util.Date;

//dto class for purchase frequency analytics
public class PurchaseFrequencyDTO {

    //attributes
    private String month;
    private Long count;

    //constructors
    public PurchaseFrequencyDTO(Object month, Long count) {  //month is an object from the repository query
        this.month = String.valueOf(month); //convert the month object to string
        this.count = count;
    }

    //getters and setters
    public String getMonth() {
        return month;
    }

    public Long getCount() {
        return count;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

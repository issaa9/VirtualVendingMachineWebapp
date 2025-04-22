package com.example.finalyearproject.dto;

public class RecommendationDTO {
    private String id;
    private String reason;

    public RecommendationDTO(String id, String reason) {
        this.id = id;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

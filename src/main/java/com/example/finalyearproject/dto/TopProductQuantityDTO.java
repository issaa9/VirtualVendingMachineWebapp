package com.example.finalyearproject.dto;

//dto object for storing top product quantities (item breakdown)
public class TopProductQuantityDTO {

    private String productId;
    private String productName;
    private Long quantity;

    public TopProductQuantityDTO(String productId, String productName, Long quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
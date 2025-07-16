package com.lessionprm.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PaymentRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private String orderInfo;
    private String extraData;
    
    // Constructors
    public PaymentRequest() {}
    
    public PaymentRequest(Long userId, Long courseId, BigDecimal amount) {
        this.userId = userId;
        this.courseId = courseId;
        this.amount = amount;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getOrderInfo() {
        return orderInfo;
    }
    
    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
    
    public String getExtraData() {
        return extraData;
    }
    
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
package com.lessionprm.backend.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreatePaymentRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Order info is required")
    private String orderInfo;

    private String extraData;

    // Constructors
    public CreatePaymentRequest() {}

    public CreatePaymentRequest(Long courseId, BigDecimal amount, String orderInfo) {
        this.courseId = courseId;
        this.amount = amount;
        this.orderInfo = orderInfo;
    }

    // Getters and Setters
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
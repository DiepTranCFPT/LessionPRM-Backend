package com.lessionprm.backend.dto.invoice;

import com.lessionprm.backend.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private String userEmail;
    private String userFullName;
    private String courseTitle;
    private BigDecimal amount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String paymentTransactionId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public InvoiceResponse() {}

    public InvoiceResponse(Invoice invoice) {
        this.id = invoice.getId();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.userEmail = invoice.getUser().getEmail();
        this.userFullName = invoice.getUser().getFullName();
        this.courseTitle = invoice.getCourse().getTitle();
        this.amount = invoice.getAmount();
        this.discountAmount = invoice.getDiscountAmount();
        this.totalAmount = invoice.getTotalAmount();
        this.status = invoice.getStatus().name();
        this.paymentMethod = invoice.getPaymentMethod() != null ? invoice.getPaymentMethod().name() : null;
        this.paymentTransactionId = invoice.getPaymentTransactionId();
        this.paidAt = invoice.getPaidAt();
        this.createdAt = invoice.getCreatedAt();
        this.updatedAt = invoice.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
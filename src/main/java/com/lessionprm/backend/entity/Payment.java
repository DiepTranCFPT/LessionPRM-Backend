package com.lessionprm.backend.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderId;

    @Column(unique = true)
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentProvider provider = PaymentProvider.MOMO;

    // MoMo specific fields
    private String momoTransId;
    private String momoResponseTime;
    private String momoMessage;
    private String momoLocalMessage;
    private String signature;

    // Refund fields
    private String refundTransId;
    private BigDecimal refundAmount;
    private LocalDateTime refundedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public Payment() {}

    public Payment(String orderId, String requestId, Invoice invoice, BigDecimal amount) {
        this.orderId = orderId;
        this.requestId = requestId;
        this.invoice = invoice;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public String getMomoTransId() {
        return momoTransId;
    }

    public void setMomoTransId(String momoTransId) {
        this.momoTransId = momoTransId;
    }

    public String getMomoResponseTime() {
        return momoResponseTime;
    }

    public void setMomoResponseTime(String momoResponseTime) {
        this.momoResponseTime = momoResponseTime;
    }

    public String getMomoMessage() {
        return momoMessage;
    }

    public void setMomoMessage(String momoMessage) {
        this.momoMessage = momoMessage;
    }

    public String getMomoLocalMessage() {
        return momoLocalMessage;
    }

    public void setMomoLocalMessage(String momoLocalMessage) {
        this.momoLocalMessage = momoLocalMessage;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRefundTransId() {
        return refundTransId;
    }

    public void setRefundTransId(String refundTransId) {
        this.refundTransId = refundTransId;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
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

    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED
    }

    public enum PaymentProvider {
        MOMO, BANK_TRANSFER, CASH
    }
}
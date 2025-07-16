package com.lessionprm.backend.dto.payment;

public class PaymentStatusResponse {

    private String orderId;
    private String status;
    private String message;
    private PaymentDetails paymentDetails;

    // Constructors
    public PaymentStatusResponse() {}

    public PaymentStatusResponse(String orderId, String status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    // Inner class for payment details
    public static class PaymentDetails {
        private String transactionId;
        private String amount;
        private String courseTitle;
        private String paymentDate;

        // Constructors
        public PaymentDetails() {}

        public PaymentDetails(String transactionId, String amount, String courseTitle, String paymentDate) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.courseTitle = courseTitle;
            this.paymentDate = paymentDate;
        }

        // Getters and Setters
        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCourseTitle() {
            return courseTitle;
        }

        public void setCourseTitle(String courseTitle) {
            this.courseTitle = courseTitle;
        }

        public String getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
        }
    }
}
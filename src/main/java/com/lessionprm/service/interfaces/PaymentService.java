package com.lessionprm.service.interfaces;

import com.lessionprm.dto.request.PaymentRequest;
import com.lessionprm.dto.response.PaymentResponse;

import java.util.Map;

public interface PaymentService {
    
    PaymentResponse createMoMoPayment(PaymentRequest request);
    
    PaymentResponse handleMoMoCallback(Map<String, String> params);
    
    PaymentResponse getPaymentStatus(String orderId);
    
    PaymentResponse processRefund(String orderId);
    
    void processSuccessfulPayment(String orderId, String transactionId);
    
    void processFailedPayment(String orderId, String reason);
}
package com.lessionprm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessionprm.config.MoMoConfig;
import com.lessionprm.dto.request.PaymentRequest;
import com.lessionprm.dto.response.PaymentResponse;
import com.lessionprm.entity.Invoice;
import com.lessionprm.exception.BadRequestException;
import com.lessionprm.service.interfaces.InvoiceService;
import com.lessionprm.service.interfaces.PaymentService;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private MoMoConfig moMoConfig;
    
    @Autowired
    private InvoiceService invoiceService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public PaymentResponse createMoMoPayment(PaymentRequest request) {
        try {
            // Create invoice first
            Invoice invoice = invoiceService.createInvoice(
                    request.getUserId(),
                    request.getCourseId(),
                    Invoice.PaymentMethod.MOMO
            );
            
            // Prepare MoMo payment request
            String orderId = invoice.getOrderId();
            String requestId = orderId;
            String amount = request.getAmount().longValue() + "";
            String orderInfo = request.getOrderInfo() != null ? request.getOrderInfo() : "Course payment: " + orderId;
            String extraData = request.getExtraData() != null ? request.getExtraData() : "";
            String requestType = "captureWallet";
            
            // Create signature
            String rawHash = "accessKey=" + moMoConfig.getAccessKey() +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + moMoConfig.getNotifyUrl() +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + moMoConfig.getPartnerCode() +
                    "&redirectUrl=" + moMoConfig.getRedirectUrl() +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;
            
            String signature = hmacSHA256(rawHash, moMoConfig.getSecretKey());
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", moMoConfig.getPartnerCode());
            requestBody.put("accessKey", moMoConfig.getAccessKey());
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", moMoConfig.getRedirectUrl());
            requestBody.put("ipnUrl", moMoConfig.getNotifyUrl());
            requestBody.put("extraData", extraData);
            requestBody.put("requestType", requestType);
            requestBody.put("signature", signature);
            requestBody.put("lang", "en");
            
            // Send request to MoMo
            String responseBody = sendHttpPost(moMoConfig.getEndpoint(), requestBody);
            
            // Parse response
            @SuppressWarnings("unchecked")
            Map<String, Object> momoResponse = objectMapper.readValue(responseBody, Map.class);
            
            PaymentResponse response = new PaymentResponse();
            response.setOrderId(orderId);
            response.setInvoiceId(invoice.getId());
            response.setAmount(request.getAmount());
            
            if ("0".equals(momoResponse.get("resultCode"))) {
                response.setPaymentUrl((String) momoResponse.get("payUrl"));
                response.setStatus("PENDING");
                response.setMessage("Payment created successfully");
                
                // Update invoice with payment URL
                invoice.setPaymentUrl(response.getPaymentUrl());
                invoiceService.updateInvoice(invoice.getId(), invoice);
                
            } else {
                response.setStatus("FAILED");
                response.setMessage((String) momoResponse.get("message"));
                response.setResultCode((String) momoResponse.get("resultCode"));
            }
            
            return response;
            
        } catch (Exception e) {
            throw new BadRequestException("Failed to create MoMo payment: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResponse handleMoMoCallback(Map<String, String> params) {
        try {
            String orderId = params.get("orderId");
            String resultCode = params.get("resultCode");
            String transId = params.get("transId");
            
            Invoice invoice = invoiceService.getInvoiceByOrderId(orderId)
                    .orElseThrow(() -> new BadRequestException("Invoice not found with orderId: " + orderId));
            
            PaymentResponse response = new PaymentResponse();
            response.setOrderId(orderId);
            response.setTransactionId(transId);
            response.setResultCode(resultCode);
            
            if ("0".equals(resultCode)) {
                // Payment successful
                invoiceService.markAsPaid(invoice.getId(), transId);
                response.setStatus("PAID");
                response.setMessage("Payment successful");
            } else {
                // Payment failed
                invoiceService.markAsFailed(invoice.getId(), "MoMo payment failed: " + resultCode);
                response.setStatus("FAILED");
                response.setMessage("Payment failed");
            }
            
            return response;
            
        } catch (Exception e) {
            throw new BadRequestException("Failed to process MoMo callback: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResponse getPaymentStatus(String orderId) {
        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId)
                .orElseThrow(() -> new BadRequestException("Invoice not found with orderId: " + orderId));
        
        PaymentResponse response = new PaymentResponse();
        response.setOrderId(orderId);
        response.setInvoiceId(invoice.getId());
        response.setAmount(invoice.getAmount());
        response.setTransactionId(invoice.getTransactionId());
        response.setStatus(invoice.getStatus().name());
        
        return response;
    }
    
    @Override
    public void processSuccessfulPayment(String orderId, String transactionId) {
        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId)
                .orElseThrow(() -> new BadRequestException("Invoice not found with orderId: " + orderId));
        
        invoiceService.markAsPaid(invoice.getId(), transactionId);
    }
    
    @Override
    public void processFailedPayment(String orderId, String reason) {
        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId)
                .orElseThrow(() -> new BadRequestException("Invoice not found with orderId: " + orderId));
        
        invoiceService.markAsFailed(invoice.getId(), reason);
    }
    
    @Override
    public PaymentResponse processRefund(String orderId) {
        try {
            Invoice invoice = invoiceService.getInvoiceByOrderId(orderId)
                    .orElseThrow(() -> new BadRequestException("Invoice not found with orderId: " + orderId));
            
            if (!Invoice.Status.PAID.equals(invoice.getStatus())) {
                throw new BadRequestException("Cannot refund invoice that is not paid");
            }
            
            // Prepare MoMo refund request
            String requestId = "refund_" + orderId + "_" + System.currentTimeMillis();
            String amount = invoice.getAmount().longValue() + "";
            String transId = invoice.getTransactionId();
            
            // Create signature for refund
            String rawHash = "accessKey=" + moMoConfig.getAccessKey() +
                    "&amount=" + amount +
                    "&description=Refund for order " + orderId +
                    "&orderId=" + orderId +
                    "&partnerCode=" + moMoConfig.getPartnerCode() +
                    "&requestId=" + requestId +
                    "&transId=" + transId;
            
            String signature = hmacSHA256(rawHash, moMoConfig.getSecretKey());
            
            // Prepare refund request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", moMoConfig.getPartnerCode());
            requestBody.put("accessKey", moMoConfig.getAccessKey());
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("transId", transId);
            requestBody.put("description", "Refund for order " + orderId);
            requestBody.put("signature", signature);
            
            // Send refund request to MoMo
            String refundEndpoint = moMoConfig.getEndpoint().replace("/create", "/refund");
            String responseBody = sendHttpPost(refundEndpoint, requestBody);
            
            // Parse response
            @SuppressWarnings("unchecked")
            Map<String, Object> momoResponse = objectMapper.readValue(responseBody, Map.class);
            
            PaymentResponse response = new PaymentResponse();
            response.setOrderId(orderId);
            response.setInvoiceId(invoice.getId());
            response.setAmount(invoice.getAmount());
            response.setTransactionId(transId);
            
            if ("0".equals(momoResponse.get("resultCode"))) {
                // Refund successful
                invoiceService.markAsRefunded(invoice.getId(), "Refund processed successfully");
                response.setStatus("REFUNDED");
                response.setMessage("Refund processed successfully");
            } else {
                response.setStatus("REFUND_FAILED");
                response.setMessage((String) momoResponse.get("message"));
                response.setResultCode((String) momoResponse.get("resultCode"));
            }
            
            return response;
            
        } catch (Exception e) {
            throw new BadRequestException("Failed to process refund: " + e.getMessage());
        }
    }
    
    private String hmacSHA256(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    private String sendHttpPost(String url, Map<String, Object> requestBody) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            httpPost.setEntity(new StringEntity(jsonBody));
            
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                return new String(response.getEntity().getContent().readAllBytes());
            }
        }
    }
}
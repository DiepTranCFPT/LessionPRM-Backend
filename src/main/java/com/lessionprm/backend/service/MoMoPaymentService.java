package com.lessionprm.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessionprm.backend.dto.payment.CreatePaymentRequest;
import com.lessionprm.backend.dto.payment.MoMoCallbackRequest;
import com.lessionprm.backend.dto.payment.MoMoPaymentResponse;
import com.lessionprm.backend.dto.payment.PaymentStatusResponse;
import com.lessionprm.backend.entity.*;
import com.lessionprm.backend.repository.CourseRepository;
import com.lessionprm.backend.repository.InvoiceRepository;
import com.lessionprm.backend.repository.PaymentRepository;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class MoMoPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MoMoPaymentService.class);

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirect-url}")
    private String redirectUrl;

    @Value("${momo.notify-url}")
    private String notifyUrl;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public MoMoPaymentResponse createPayment(CreatePaymentRequest request, User user) {
        try {
            // Find course
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Create invoice
            String invoiceNumber = generateInvoiceNumber();
            Invoice invoice = new Invoice(invoiceNumber, user, course, request.getAmount(), request.getAmount());
            invoice.setPaymentMethod(Invoice.PaymentMethod.MOMO);
            invoice = invoiceRepository.save(invoice);

            // Generate order ID and request ID
            String orderId = generateOrderId();
            String requestId = UUID.randomUUID().toString();

            // Create payment record
            Payment payment = new Payment(orderId, requestId, invoice, request.getAmount());
            payment = paymentRepository.save(payment);

            // Build MoMo request
            Map<String, Object> momoRequest = buildMoMoRequest(orderId, requestId, request.getAmount(), request.getOrderInfo());

            // Send request to MoMo
            MoMoPaymentResponse response = sendMoMoRequest(momoRequest);

            // Update payment with response details
            payment.setSignature(response.getSignature());
            paymentRepository.save(payment);

            logger.info("Created MoMo payment request for order ID: {}", orderId);
            return response;

        } catch (Exception e) {
            logger.error("Error creating MoMo payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }

    public void handleCallback(MoMoCallbackRequest callback) {
        try {
            // Verify signature
            if (!verifySignature(callback)) {
                logger.warn("Invalid signature for callback order ID: {}", callback.getOrderId());
                throw new RuntimeException("Invalid signature");
            }

            // Find payment
            Payment payment = paymentRepository.findByOrderId(callback.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Update payment status
            if ("0".equals(callback.getResultCode())) {
                // Success
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setMomoTransId(callback.getTransId());
                
                // Update invoice
                Invoice invoice = payment.getInvoice();
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
                invoice.setPaymentTransactionId(callback.getTransId());
                invoice.setPaidAt(LocalDateTime.now());
                invoiceRepository.save(invoice);

                // Enroll user in course
                enrollUserInCourse(invoice.getUser(), invoice.getCourse());

                logger.info("Payment successful for order ID: {}", callback.getOrderId());
            } else {
                // Failed
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.getInvoice().setStatus(Invoice.InvoiceStatus.FAILED);
                invoiceRepository.save(payment.getInvoice());

                logger.warn("Payment failed for order ID: {}, result code: {}", 
                           callback.getOrderId(), callback.getResultCode());
            }

            payment.setMomoMessage(callback.getMessage());
            payment.setMomoResponseTime(callback.getResponseTime());
            paymentRepository.save(payment);

        } catch (Exception e) {
            logger.error("Error handling MoMo callback: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to handle callback: " + e.getMessage());
        }
    }

    public PaymentStatusResponse getPaymentStatus(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        PaymentStatusResponse response = new PaymentStatusResponse();
        response.setOrderId(orderId);
        response.setStatus(payment.getStatus().name());
        response.setMessage(payment.getMomoMessage());

        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            PaymentStatusResponse.PaymentDetails details = new PaymentStatusResponse.PaymentDetails();
            details.setTransactionId(payment.getMomoTransId());
            details.setAmount(payment.getAmount().toString());
            details.setCourseTitle(payment.getInvoice().getCourse().getTitle());
            details.setPaymentDate(payment.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.setPaymentDetails(details);
        }

        return response;
    }

    private Map<String, Object> buildMoMoRequest(String orderId, String requestId, BigDecimal amount, String orderInfo) {
        Map<String, Object> request = new HashMap<>();
        request.put("partnerCode", partnerCode);
        request.put("requestId", requestId);
        request.put("amount", amount.longValue());
        request.put("orderId", orderId);
        request.put("orderInfo", orderInfo);
        request.put("redirectUrl", redirectUrl);
        request.put("ipnUrl", notifyUrl);
        request.put("requestType", "captureWallet");
        request.put("extraData", "");
        request.put("lang", "en");

        // Generate signature
        String signature = generateSignature(request);
        request.put("signature", signature);

        return request;
    }

    private String generateSignature(Map<String, Object> params) {
        try {
            String rawData = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                accessKey,
                params.get("amount"),
                params.get("extraData"),
                params.get("ipnUrl"),
                params.get("orderId"),
                params.get("orderInfo"),
                params.get("partnerCode"),
                params.get("redirectUrl"),
                params.get("requestId"),
                params.get("requestType")
            );

            return hmacSHA256(rawData, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    private boolean verifySignature(MoMoCallbackRequest callback) {
        try {
            String rawData = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey,
                callback.getAmount(),
                callback.getExtraData(),
                callback.getMessage(),
                callback.getOrderId(),
                callback.getOrderInfo(),
                callback.getOrderType(),
                callback.getPartnerCode(),
                callback.getPayType(),
                callback.getRequestId(),
                callback.getResponseTime(),
                callback.getResultCode(),
                callback.getTransId()
            );

            String expectedSignature = hmacSHA256(rawData, secretKey);
            return expectedSignature.equals(callback.getSignature());
        } catch (Exception e) {
            logger.error("Error verifying signature: {}", e.getMessage());
            return false;
        }
    }

    private MoMoPaymentResponse sendMoMoRequest(Map<String, Object> request) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(endpoint);
            
            String jsonRequest = objectMapper.writeValueAsString(request);
            StringEntity entity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            String responseBody = httpClient.execute(httpPost, response -> {
                return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            });

            return objectMapper.readValue(responseBody, MoMoPaymentResponse.class);
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String generateInvoiceNumber() {
        return "INV" + System.currentTimeMillis();
    }

    private String generateOrderId() {
        return "ORDER" + System.currentTimeMillis();
    }

    private void enrollUserInCourse(User user, Course course) {
        // This would be handled by the enrollment service
        // For now, we'll just log it
        logger.info("User {} enrolled in course {}", user.getEmail(), course.getTitle());
    }
}
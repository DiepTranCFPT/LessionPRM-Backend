package com.lessionprm.controller;

import com.lessionprm.dto.request.PaymentRequest;
import com.lessionprm.dto.response.PaymentResponse;
import com.lessionprm.service.interfaces.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@Tag(name = "Payment", description = "Payment processing APIs")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping("/momo/create")
    @Operation(summary = "Create MoMo payment", description = "Create MoMo payment for course purchase")
    public ResponseEntity<PaymentResponse> createMoMoPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createMoMoPayment(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/momo/callback")
    @Operation(summary = "MoMo payment callback", description = "Handle MoMo payment callback")
    public ResponseEntity<PaymentResponse> handleMoMoCallback(@RequestParam Map<String, String> params) {
        PaymentResponse response = paymentService.handleMoMoCallback(params);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/momo/status/{orderId}")
    @Operation(summary = "Get payment status", description = "Get payment status by order ID")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String orderId) {
        PaymentResponse response = paymentService.getPaymentStatus(orderId);
        return ResponseEntity.ok(response);
    }
}
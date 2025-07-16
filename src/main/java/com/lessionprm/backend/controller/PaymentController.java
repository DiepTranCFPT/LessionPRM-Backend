package com.lessionprm.backend.controller;

import com.lessionprm.backend.dto.payment.CreatePaymentRequest;
import com.lessionprm.backend.dto.payment.MoMoCallbackRequest;
import com.lessionprm.backend.dto.payment.MoMoPaymentResponse;
import com.lessionprm.backend.dto.payment.PaymentStatusResponse;
import com.lessionprm.backend.entity.User;
import com.lessionprm.backend.service.MoMoPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    @Autowired
    private MoMoPaymentService moMoPaymentService;

    @PostMapping("/momo/create")
    @Operation(summary = "Create MoMo payment")
    public ResponseEntity<MoMoPaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @AuthenticationPrincipal User user) {
        MoMoPaymentResponse response = moMoPaymentService.createPayment(request, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/momo/callback")
    @Operation(summary = "Handle MoMo payment callback")
    public ResponseEntity<String> handleCallback(@RequestBody MoMoCallbackRequest request) {
        moMoPaymentService.handleCallback(request);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/momo/status/{orderId}")
    @Operation(summary = "Get payment status")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable String orderId) {
        PaymentStatusResponse response = moMoPaymentService.getPaymentStatus(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/momo/refund")
    @Operation(summary = "Process MoMo refund")
    public ResponseEntity<Map<String, String>> processRefund(@RequestBody Map<String, String> request) {
        // Implementation for refund would go here
        // For now, return a placeholder response
        return ResponseEntity.ok(Map.of("message", "Refund feature not implemented yet"));
    }
}
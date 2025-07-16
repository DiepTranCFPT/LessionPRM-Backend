package com.lessionprm.backend.controller;

import com.lessionprm.backend.dto.invoice.InvoiceResponse;
import com.lessionprm.backend.entity.Invoice;
import com.lessionprm.backend.entity.User;
import com.lessionprm.backend.repository.InvoiceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@Tag(name = "Invoice", description = "Invoice management APIs")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping
    @Operation(summary = "Get user's invoices")
    public ResponseEntity<Page<InvoiceResponse>> getUserInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User user) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Invoice> invoicePage;
        
        if (status != null && !status.trim().isEmpty()) {
            Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status.toUpperCase());
            invoicePage = invoiceRepository.findByUserAndStatus(user, invoiceStatus, pageable);
        } else {
            invoicePage = invoiceRepository.findByUser(user, pageable);
        }
        
        Page<InvoiceResponse> responsePage = invoicePage.map(InvoiceResponse::new);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        // Check if user owns this invoice or is admin
        if (!invoice.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }
        
        return ResponseEntity.ok(new InvoiceResponse(invoice));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all invoices (Admin only)")
    public ResponseEntity<Page<InvoiceResponse>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Invoice> invoicePage;
        
        if (status != null && !status.trim().isEmpty()) {
            Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status.toUpperCase());
            invoicePage = invoiceRepository.findByStatus(invoiceStatus, pageable);
        } else {
            invoicePage = invoiceRepository.findAll(pageable);
        }
        
        Page<InvoiceResponse> responsePage = invoicePage.map(InvoiceResponse::new);
        return ResponseEntity.ok(responsePage);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update invoice status (Admin only)")
    public ResponseEntity<Map<String, String>> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        String status = request.get("status");
        invoice.setStatus(Invoice.InvoiceStatus.valueOf(status.toUpperCase()));
        
        if ("PAID".equals(status.toUpperCase()) && invoice.getPaidAt() == null) {
            invoice.setPaidAt(LocalDateTime.now());
        }
        
        invoiceRepository.save(invoice);
        
        return ResponseEntity.ok(Map.of("message", "Invoice status updated successfully"));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get invoice statistics (Admin only)")
    public ResponseEntity<Map<String, Object>> getInvoiceStatistics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDateTime startDate;
        LocalDateTime endDate;
        
        if (year != null && month != null) {
            startDate = LocalDateTime.of(year, month, 1, 0, 0);
            endDate = startDate.plusMonths(1).minusSeconds(1);
        } else if (year != null) {
            startDate = LocalDateTime.of(year, 1, 1, 0, 0);
            endDate = startDate.plusYears(1).minusSeconds(1);
        } else {
            // Current month
            LocalDateTime now = LocalDateTime.now();
            startDate = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);
            endDate = startDate.plusMonths(1).minusSeconds(1);
        }
        
        var totalRevenue = invoiceRepository.getTotalRevenue(startDate, endDate);
        var paidInvoicesCount = invoiceRepository.countPaidInvoices(startDate, endDate);
        
        Map<String, Object> statistics = Map.of(
            "totalRevenue", totalRevenue != null ? totalRevenue : 0,
            "paidInvoicesCount", paidInvoicesCount,
            "period", Map.of(
                "startDate", startDate,
                "endDate", endDate
            )
        );
        
        return ResponseEntity.ok(statistics);
    }
}
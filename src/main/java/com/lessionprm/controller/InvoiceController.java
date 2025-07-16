package com.lessionprm.controller;

import com.lessionprm.entity.Invoice;
import com.lessionprm.entity.User;
import com.lessionprm.exception.ResourceNotFoundException;
import com.lessionprm.service.interfaces.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
@Tag(name = "Invoices", description = "Invoice management APIs")
public class InvoiceController {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @GetMapping
    @Operation(summary = "Get user invoices", description = "Get invoices for current user")
    public ResponseEntity<Page<Invoice>> getUserInvoices(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<Invoice> invoices = invoiceService.getInvoicesByUserId(user.getId(), pageable);
        return ResponseEntity.ok(invoices);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID", description = "Get invoice details by ID")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id, Authentication authentication) {
        Invoice invoice = invoiceService.getInvoiceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        User user = (User) authentication.getPrincipal();
        
        // Users can only access their own invoices, admins can access any
        if (!user.getRole().equals(User.Role.ADMIN) && !invoice.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Invoice not found with id: " + id);
        }
        
        return ResponseEntity.ok(invoice);
    }
    
    @PostMapping
    @Operation(summary = "Create invoice", description = "Create new invoice for course purchase")
    public ResponseEntity<Invoice> createInvoice(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Long courseId = Long.valueOf(request.get("courseId").toString());
        String paymentMethodStr = request.get("paymentMethod").toString();
        Invoice.PaymentMethod paymentMethod = Invoice.PaymentMethod.valueOf(paymentMethodStr);
        
        Invoice invoice = invoiceService.createInvoice(user.getId(), courseId, paymentMethod);
        return ResponseEntity.ok(invoice);
    }
    
    @PutMapping("/{id}/paid")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark invoice as paid", description = "Mark invoice as paid (Admin only)")
    public ResponseEntity<Invoice> markAsPaid(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String transactionId = request.get("transactionId");
        Invoice invoice = invoiceService.markAsPaid(id, transactionId);
        return ResponseEntity.ok(invoice);
    }
    
    @PutMapping("/{id}/failed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark invoice as failed", description = "Mark invoice as failed (Admin only)")
    public ResponseEntity<Invoice> markAsFailed(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String reason = request.get("reason");
        Invoice invoice = invoiceService.markAsFailed(id, reason);
        return ResponseEntity.ok(invoice);
    }
    
    @PutMapping("/{id}/cancelled")
    @Operation(summary = "Cancel invoice", description = "Cancel invoice")
    public ResponseEntity<Invoice> markAsCancelled(@PathVariable Long id, Authentication authentication) {
        Invoice invoice = invoiceService.getInvoiceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        User user = (User) authentication.getPrincipal();
        
        // Users can only cancel their own invoices, admins can cancel any
        if (!user.getRole().equals(User.Role.ADMIN) && !invoice.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Invoice not found with id: " + id);
        }
        
        Invoice updatedInvoice = invoiceService.markAsCancelled(id);
        return ResponseEntity.ok(updatedInvoice);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all invoices", description = "Get all invoices with pagination (Admin only)")
    public ResponseEntity<Page<Invoice>> getAllInvoices(Pageable pageable) {
        Page<Invoice> invoices = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(invoices);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get invoices by status", description = "Get invoices by status (Admin only)")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable Invoice.Status status) {
        List<Invoice> invoices = invoiceService.getInvoicesByStatus(status);
        return ResponseEntity.ok(invoices);
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get invoice statistics", description = "Get invoice statistics (Admin only)")
    public ResponseEntity<Map<String, Object>> getInvoiceStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        BigDecimal totalRevenue = invoiceService.getTotalRevenueByDateRange(startDate, endDate);
        long paidCount = invoiceService.countPaidInvoicesByDateRange(startDate, endDate);
        long pendingCount = invoiceService.countByStatus(Invoice.Status.PENDING);
        long failedCount = invoiceService.countByStatus(Invoice.Status.FAILED);
        long cancelledCount = invoiceService.countByStatus(Invoice.Status.CANCELLED);
        
        List<Object[]> paymentMethodStats = invoiceService.getPaymentMethodStatistics();
        
        Map<String, Object> statistics = Map.of(
                "totalRevenue", totalRevenue,
                "paidInvoices", paidCount,
                "pendingInvoices", pendingCount,
                "failedInvoices", failedCount,
                "cancelledInvoices", cancelledCount,
                "paymentMethodStatistics", paymentMethodStats,
                "dateRange", Map.of("start", startDate, "end", endDate)
        );
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/user/{userId}/courses")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user's purchased courses", description = "Get courses purchased by user (Admin only)")
    public ResponseEntity<List<Long>> getUserPurchasedCourses(@PathVariable Long userId) {
        List<Long> courseIds = invoiceService.getPaidCourseIdsByUser(userId);
        return ResponseEntity.ok(courseIds);
    }
    
    @PostMapping("/process-expired")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Process expired invoices", description = "Process and cancel expired invoices (Admin only)")
    public ResponseEntity<Map<String, String>> processExpiredInvoices() {
        invoiceService.processExpiredInvoices();
        return ResponseEntity.ok(Map.of("message", "Expired invoices processed successfully"));
    }
}
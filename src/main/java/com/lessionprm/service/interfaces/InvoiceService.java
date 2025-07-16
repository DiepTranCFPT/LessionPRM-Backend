package com.lessionprm.service.interfaces;

import com.lessionprm.entity.Invoice;
import com.lessionprm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    
    Invoice createInvoice(Long userId, Long courseId, Invoice.PaymentMethod paymentMethod);
    
    Invoice updateInvoice(Long id, Invoice invoice);
    
    Optional<Invoice> getInvoiceById(Long id);
    
    List<Invoice> getAllInvoices();
    
    Page<Invoice> getAllInvoices(Pageable pageable);
    
    List<Invoice> getInvoicesByUser(User user);
    
    Page<Invoice> getInvoicesByUser(User user, Pageable pageable);
    
    List<Invoice> getInvoicesByUserId(Long userId);
    
    Page<Invoice> getInvoicesByUserId(Long userId, Pageable pageable);
    
    List<Invoice> getInvoicesByStatus(Invoice.Status status);
    
    Optional<Invoice> getInvoiceByOrderId(String orderId);
    
    Optional<Invoice> getInvoiceByTransactionId(String transactionId);
    
    Invoice markAsPaid(Long id, String transactionId);
    
    Invoice markAsFailed(Long id, String reason);
    
    Invoice markAsCancelled(Long id);
    
    void deleteInvoice(Long id);
    
    BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    long countPaidInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    boolean hasUserPurchasedCourse(Long userId, Long courseId);
    
    List<Long> getPaidCourseIdsByUser(Long userId);
    
    void processExpiredInvoices();
    
    long countByStatus(Invoice.Status status);
    
    List<Object[]> getPaymentMethodStatistics();
}
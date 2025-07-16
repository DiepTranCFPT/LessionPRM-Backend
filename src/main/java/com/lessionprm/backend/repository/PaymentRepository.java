package com.lessionprm.backend.repository;

import com.lessionprm.backend.entity.Invoice;
import com.lessionprm.backend.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(String orderId);
    
    Optional<Payment> findByRequestId(String requestId);
    
    Optional<Payment> findByInvoice(Invoice invoice);
    
    Page<Payment> findByStatus(Payment.PaymentStatus status, Pageable pageable);
    
    Page<Payment> findByProvider(Payment.PaymentProvider provider, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Page<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate, 
                                 Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'SUCCESS' AND p.updatedAt BETWEEN :startDate AND :endDate")
    Long countSuccessfulPayments(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    Optional<Payment> findByMomoTransId(String momoTransId);
}
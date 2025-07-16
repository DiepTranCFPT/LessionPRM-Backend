package com.lessionprm.repository;

import com.lessionprm.entity.Invoice;
import com.lessionprm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    List<Invoice> findByUser(User user);
    
    Page<Invoice> findByUser(User user, Pageable pageable);
    
    List<Invoice> findByUserId(Long userId);
    
    Page<Invoice> findByUserId(Long userId, Pageable pageable);
    
    List<Invoice> findByStatus(Invoice.Status status);
    
    Page<Invoice> findByStatus(Invoice.Status status, Pageable pageable);
    
    List<Invoice> findByPaymentMethod(Invoice.PaymentMethod paymentMethod);
    
    Optional<Invoice> findByOrderId(String orderId);
    
    Optional<Invoice> findByTransactionId(String transactionId);
    
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId AND i.status = :status")
    List<Invoice> findByUserIdAndStatus(@Param("userId") Long userId, 
                                       @Param("status") Invoice.Status status);
    
    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate")
    List<Invoice> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate AND i.status = :status")
    List<Invoice> findByCreatedAtBetweenAndStatus(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate,
                                                 @Param("status") Invoice.Status status);
    
    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.status = 'PAID' AND i.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'PAID' AND i.createdAt BETWEEN :startDate AND :endDate")
    long countPaidInvoicesByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.user.id = :userId AND i.course.id = :courseId AND i.status = 'PAID'")
    long countPaidInvoicesByUserAndCourse(@Param("userId") Long userId, 
                                         @Param("courseId") Long courseId);
    
    @Query("SELECT DISTINCT i.course.id FROM Invoice i WHERE i.user.id = :userId AND i.status = 'PAID'")
    List<Long> findPaidCourseIdsByUser(@Param("userId") Long userId);
    
    @Query("SELECT i FROM Invoice i WHERE i.status IN ('PENDING', 'FAILED') AND i.createdAt < :expiryDate")
    List<Invoice> findExpiredInvoices(@Param("expiryDate") LocalDateTime expiryDate);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    long countByStatus(@Param("status") Invoice.Status status);
    
    @Query("SELECT i.paymentMethod, COUNT(i) FROM Invoice i WHERE i.status = 'PAID' GROUP BY i.paymentMethod")
    List<Object[]> getPaymentMethodStatistics();
}
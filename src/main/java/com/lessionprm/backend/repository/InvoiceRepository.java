package com.lessionprm.backend.repository;

import com.lessionprm.backend.entity.Invoice;
import com.lessionprm.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    Page<Invoice> findByUser(User user, Pageable pageable);
    
    Page<Invoice> findByStatus(Invoice.InvoiceStatus status, Pageable pageable);
    
    Page<Invoice> findByUserAndStatus(User user, Invoice.InvoiceStatus status, Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate")
    Page<Invoice> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate, 
                                 Pageable pageable);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'PAID' AND i.paidAt BETWEEN :startDate AND :endDate")
    Long countPaidInvoices(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND YEAR(i.paidAt) = :year AND MONTH(i.paidAt) = :month")
    BigDecimal getMonthlyRevenue(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND YEAR(i.paidAt) = :year")
    BigDecimal getYearlyRevenue(@Param("year") int year);
}
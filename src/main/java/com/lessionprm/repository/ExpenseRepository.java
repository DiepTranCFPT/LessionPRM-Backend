package com.lessionprm.repository;

import com.lessionprm.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByCategory(String category);
    
    Page<Expense> findByCategory(String category, Pageable pageable);
    
    List<Expense> findByIsApproved(Boolean isApproved);
    
    Page<Expense> findByIsApproved(Boolean isApproved, Pageable pageable);
    
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    Page<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate AND e.isApproved = :isApproved")
    List<Expense> findByDateBetweenAndIsApproved(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate,
                                                @Param("isApproved") Boolean isApproved);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.isApproved = true AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalApprovedExpensesByDateRange(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category = :category AND e.isApproved = true")
    BigDecimal getTotalApprovedExpensesByCategory(@Param("category") String category);
    
    @Query("SELECT COUNT(e) FROM Expense e WHERE e.isApproved = :isApproved")
    long countByIsApproved(@Param("isApproved") Boolean isApproved);
    
    @Query("SELECT DISTINCT e.category FROM Expense e ORDER BY e.category")
    List<String> findAllCategories();
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.isApproved = true GROUP BY e.category")
    List<Object[]> getExpensesByCategory();
    
    @Query("SELECT e FROM Expense e WHERE e.amount > :amount AND e.isApproved = false")
    List<Expense> findPendingExpensesAboveAmount(@Param("amount") BigDecimal amount);
    
    @Query("SELECT YEAR(e.date), MONTH(e.date), SUM(e.amount) FROM Expense e WHERE e.isApproved = true " +
           "GROUP BY YEAR(e.date), MONTH(e.date) ORDER BY YEAR(e.date), MONTH(e.date)")
    List<Object[]> getMonthlyExpenseSummary();
}
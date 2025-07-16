package com.lessionprm.backend.repository;

import com.lessionprm.backend.entity.Expense;
import com.lessionprm.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    Page<Expense> findByCategory(String category, Pageable pageable);
    
    Page<Expense> findByCreatedBy(User createdBy, Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    Page<Expense> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate, 
                                 Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.category = :category AND e.expenseDate BETWEEN :startDate AND :endDate")
    Page<Expense> findByCategoryAndDateRange(@Param("category") String category,
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate, 
                                           Pageable pageable);
    
    @Query("SELECT DISTINCT e.category FROM Expense e ORDER BY e.category")
    List<String> findDistinctCategories();
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpenses(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE YEAR(e.expenseDate) = :year AND MONTH(e.expenseDate) = :month")
    BigDecimal getMonthlyExpenses(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE YEAR(e.expenseDate) = :year")
    BigDecimal getYearlyExpenses(@Param("year") int year);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate GROUP BY e.category")
    List<Object[]> getExpensesByCategory(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
package com.lessionprm.service.interfaces;

import com.lessionprm.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    
    Expense createExpense(Expense expense);
    
    Expense updateExpense(Long id, Expense expense);
    
    Optional<Expense> getExpenseById(Long id);
    
    List<Expense> getAllExpenses();
    
    Page<Expense> getAllExpenses(Pageable pageable);
    
    List<Expense> getExpensesByCategory(String category);
    
    List<Expense> getApprovedExpenses();
    
    Page<Expense> getApprovedExpenses(Pageable pageable);
    
    List<Expense> getPendingExpenses();
    
    Page<Expense> getPendingExpenses(Pageable pageable);
    
    List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate);
    
    Expense approveExpense(Long id, String approvedBy);
    
    Expense rejectExpense(Long id);
    
    void deleteExpense(Long id);
    
    BigDecimal getTotalApprovedExpensesByDateRange(LocalDate startDate, LocalDate endDate);
    
    BigDecimal getTotalApprovedExpensesByCategory(String category);
    
    long countByApprovalStatus(Boolean isApproved);
    
    List<String> getAllCategories();
    
    List<Object[]> getExpensesByCategory();
    
    List<Expense> getPendingExpensesAboveAmount(BigDecimal amount);
    
    List<Object[]> getMonthlyExpenseSummary();
}
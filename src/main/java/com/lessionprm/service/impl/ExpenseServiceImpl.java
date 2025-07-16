package com.lessionprm.service.impl;

import com.lessionprm.entity.Expense;
import com.lessionprm.exception.ResourceNotFoundException;
import com.lessionprm.repository.ExpenseRepository;
import com.lessionprm.service.interfaces.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Override
    public Expense createExpense(Expense expense) {
        expense.setIsApproved(false);
        return expenseRepository.save(expense);
    }
    
    @Override
    public Expense updateExpense(Long id, Expense expense) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        
        existingExpense.setDescription(expense.getDescription());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setDate(expense.getDate());
        existingExpense.setReceiptUrl(expense.getReceiptUrl());
        existingExpense.setNotes(expense.getNotes());
        
        return expenseRepository.save(existingExpense);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Expense> getAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByCategory(String category) {
        return expenseRepository.findByCategory(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Expense> getApprovedExpenses() {
        return expenseRepository.findByIsApproved(true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Expense> getApprovedExpenses(Pageable pageable) {
        return expenseRepository.findByIsApproved(true, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Expense> getPendingExpenses() {
        return expenseRepository.findByIsApproved(false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Expense> getPendingExpenses(Pageable pageable) {
        return expenseRepository.findByIsApproved(false, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByDateBetween(startDate, endDate);
    }
    
    @Override
    public Expense approveExpense(Long id, String approvedBy) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        
        expense.setIsApproved(true);
        expense.setApprovedBy(approvedBy);
        expense.setApprovedAt(LocalDateTime.now());
        
        return expenseRepository.save(expense);
    }
    
    @Override
    public Expense rejectExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        
        expense.setIsApproved(false);
        expense.setApprovedBy(null);
        expense.setApprovedAt(null);
        
        return expenseRepository.save(expense);
    }
    
    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalApprovedExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.getTotalApprovedExpensesByDateRange(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalApprovedExpensesByCategory(String category) {
        BigDecimal total = expenseRepository.getTotalApprovedExpensesByCategory(category);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByApprovalStatus(Boolean isApproved) {
        return expenseRepository.countByIsApproved(isApproved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return expenseRepository.findAllCategories();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getExpensesByCategory() {
        return expenseRepository.getExpensesByCategory();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Expense> getPendingExpensesAboveAmount(BigDecimal amount) {
        return expenseRepository.findPendingExpensesAboveAmount(amount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyExpenseSummary() {
        return expenseRepository.getMonthlyExpenseSummary();
    }
}
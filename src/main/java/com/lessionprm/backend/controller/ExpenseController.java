package com.lessionprm.backend.controller;

import com.lessionprm.backend.dto.expense.CreateExpenseRequest;
import com.lessionprm.backend.dto.expense.ExpenseResponse;
import com.lessionprm.backend.entity.Expense;
import com.lessionprm.backend.entity.User;
import com.lessionprm.backend.repository.ExpenseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Expense", description = "Expense management APIs (Admin only)")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping
    @Operation(summary = "Get all expenses")
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("expenseDate").descending());
        Page<Expense> expensePage;
        
        if (category != null && !category.trim().isEmpty() && month != null && year != null) {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
            expensePage = expenseRepository.findByCategoryAndDateRange(category, startDate, endDate, pageable);
        } else if (month != null && year != null) {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
            expensePage = expenseRepository.findByDateRange(startDate, endDate, pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            expensePage = expenseRepository.findByCategory(category, pageable);
        } else {
            expensePage = expenseRepository.findAll(pageable);
        }
        
        Page<ExpenseResponse> responsePage = expensePage.map(ExpenseResponse::new);
        return ResponseEntity.ok(responsePage);
    }

    @PostMapping
    @Operation(summary = "Create new expense")
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal User user) {
        
        Expense expense = new Expense();
        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setExpenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : LocalDateTime.now());
        expense.setCreatedBy(user);
        
        expense = expenseRepository.save(expense);
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update expense")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody CreateExpenseRequest request) {
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        }
        
        expense = expenseRepository.save(expense);
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        expenseRepository.delete(expense);
        return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all expense categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = expenseRepository.findDistinctCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/monthly-total")
    @Operation(summary = "Get monthly expense total")
    public ResponseEntity<Map<String, Object>> getMonthlyTotal(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDateTime now = LocalDateTime.now();
        int targetMonth = month != null ? month : now.getMonthValue();
        int targetYear = year != null ? year : now.getYear();
        
        BigDecimal monthlyTotal = expenseRepository.getMonthlyExpenses(targetYear, targetMonth);
        
        Map<String, Object> response = Map.of(
            "month", targetMonth,
            "year", targetYear,
            "totalAmount", monthlyTotal != null ? monthlyTotal : BigDecimal.ZERO
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/yearly-total")
    @Operation(summary = "Get yearly expense total")
    public ResponseEntity<Map<String, Object>> getYearlyTotal(
            @RequestParam(required = false) Integer year) {
        
        int targetYear = year != null ? year : LocalDateTime.now().getYear();
        BigDecimal yearlyTotal = expenseRepository.getYearlyExpenses(targetYear);
        
        Map<String, Object> response = Map.of(
            "year", targetYear,
            "totalAmount", yearlyTotal != null ? yearlyTotal : BigDecimal.ZERO
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-category")
    @Operation(summary = "Get expenses grouped by category")
    public ResponseEntity<List<Map<String, Object>>> getExpensesByCategory(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDateTime startDate;
        LocalDateTime endDate;
        
        if (month != null && year != null) {
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
        
        List<Object[]> results = expenseRepository.getExpensesByCategory(startDate, endDate);
        List<Map<String, Object>> response = results.stream()
                .map(result -> Map.of(
                    "category", result[0],
                    "totalAmount", result[1]
                ))
                .toList();
        
        return ResponseEntity.ok(response);
    }
}
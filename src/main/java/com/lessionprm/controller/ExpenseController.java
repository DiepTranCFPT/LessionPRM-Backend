package com.lessionprm.controller;

import com.lessionprm.entity.Expense;
import com.lessionprm.entity.User;
import com.lessionprm.exception.ResourceNotFoundException;
import com.lessionprm.service.interfaces.ExpenseService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Expense management APIs")
@PreAuthorize("hasRole('ADMIN')")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @GetMapping
    @Operation(summary = "List expenses", description = "Get all expenses with pagination (Admin only)")
    public ResponseEntity<Page<Expense>> getAllExpenses(Pageable pageable) {
        Page<Expense> expenses = expenseService.getAllExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID", description = "Get expense details by ID (Admin only)")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        return ResponseEntity.ok(expense);
    }
    
    @PostMapping
    @Operation(summary = "Create expense", description = "Create new expense (Admin only)")
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.ok(createdExpense);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update expense", description = "Update expense details (Admin only)")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
        Expense updatedExpense = expenseService.updateExpense(id, expense);
        return ResponseEntity.ok(updatedExpense);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense", description = "Delete expense (Admin only)")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
    }
    
    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve expense", description = "Approve expense (Admin only)")
    public ResponseEntity<Expense> approveExpense(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Expense approvedExpense = expenseService.approveExpense(id, user.getUsername());
        return ResponseEntity.ok(approvedExpense);
    }
    
    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject expense", description = "Reject expense (Admin only)")
    public ResponseEntity<Expense> rejectExpense(@PathVariable Long id) {
        Expense rejectedExpense = expenseService.rejectExpense(id);
        return ResponseEntity.ok(rejectedExpense);
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get expense categories", description = "Get all expense categories (Admin only)")
    public ResponseEntity<List<String>> getExpenseCategories() {
        List<String> categories = expenseService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get expenses by category", description = "Get expenses by category (Admin only)")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable String category) {
        List<Expense> expenses = expenseService.getExpensesByCategory(category);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/approved")
    @Operation(summary = "Get approved expenses", description = "Get approved expenses with pagination (Admin only)")
    public ResponseEntity<Page<Expense>> getApprovedExpenses(Pageable pageable) {
        Page<Expense> expenses = expenseService.getApprovedExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/pending")
    @Operation(summary = "Get pending expenses", description = "Get pending expenses with pagination (Admin only)")
    public ResponseEntity<Page<Expense>> getPendingExpenses(Pageable pageable) {
        Page<Expense> expenses = expenseService.getPendingExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get expenses by date range", description = "Get expenses by date range (Admin only)")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Expense> expenses = expenseService.getExpensesByDateRange(startDate, endDate);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/totals")
    @Operation(summary = "Get expense totals by category", description = "Get expense totals by category (Admin only)")
    public ResponseEntity<Map<String, Object>> getExpenseTotals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        BigDecimal totalApproved = expenseService.getTotalApprovedExpensesByDateRange(startDate, endDate);
        List<Object[]> categoryTotals = expenseService.getExpensesByCategory();
        List<Object[]> monthlySummary = expenseService.getMonthlyExpenseSummary();
        
        BigDecimal categoryTotal = BigDecimal.ZERO;
        if (category != null && !category.isEmpty()) {
            categoryTotal = expenseService.getTotalApprovedExpensesByCategory(category);
        }
        
        long approvedCount = expenseService.countByApprovalStatus(true);
        long pendingCount = expenseService.countByApprovalStatus(false);
        
        Map<String, Object> totals = Map.of(
                "totalApproved", totalApproved,
                "categoryTotal", categoryTotal,
                "approvedCount", approvedCount,
                "pendingCount", pendingCount,
                "categoryBreakdown", categoryTotals,
                "monthlySummary", monthlySummary,
                "dateRange", Map.of("start", startDate, "end", endDate)
        );
        
        return ResponseEntity.ok(totals);
    }
    
    @GetMapping("/summary")
    @Operation(summary = "Get expense summary", description = "Get expense summary statistics (Admin only)")
    public ResponseEntity<Map<String, Object>> getExpenseSummary() {
        List<Object[]> categoryBreakdown = expenseService.getExpensesByCategory();
        List<Object[]> monthlySummary = expenseService.getMonthlyExpenseSummary();
        long approvedCount = expenseService.countByApprovalStatus(true);
        long pendingCount = expenseService.countByApprovalStatus(false);
        
        Map<String, Object> summary = Map.of(
                "categoryBreakdown", categoryBreakdown,
                "monthlySummary", monthlySummary,
                "approvedCount", approvedCount,
                "pendingCount", pendingCount,
                "totalExpenses", approvedCount + pendingCount
        );
        
        return ResponseEntity.ok(summary);
    }
}
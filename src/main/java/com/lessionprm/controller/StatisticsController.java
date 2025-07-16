package com.lessionprm.controller;

import com.lessionprm.entity.Course;
import com.lessionprm.entity.Invoice;
import com.lessionprm.entity.User;
import com.lessionprm.service.interfaces.CourseService;
import com.lessionprm.service.interfaces.ExpenseService;
import com.lessionprm.service.interfaces.InvoiceService;
import com.lessionprm.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Statistics and analytics APIs")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ExpenseService expenseService;
    
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard data", description = "Get dashboard statistics (Admin only)")
    public ResponseEntity<Map<String, Object>> getDashboardData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        // Revenue data
        BigDecimal totalRevenue = invoiceService.getTotalRevenueByDateRange(startDate, endDate);
        long paidInvoices = invoiceService.countPaidInvoicesByDateRange(startDate, endDate);
        
        // Course data
        long totalCourses = courseService.countActiveCourses();
        List<Course> availableCourses = courseService.getAvailableCourses();
        
        // User data
        long totalUsers = userService.countUsersByRole(User.Role.USER);
        long adminUsers = userService.countUsersByRole(User.Role.ADMIN);
        long newUsers = userService.countNewUsersBetween(startDate, endDate);
        
        // Invoice data
        long pendingInvoices = invoiceService.countByStatus(Invoice.Status.PENDING);
        long failedInvoices = invoiceService.countByStatus(Invoice.Status.FAILED);
        
        // Expense data
        LocalDate expenseStartDate = startDate.toLocalDate();
        LocalDate expenseEndDate = endDate.toLocalDate();
        BigDecimal totalExpenses = expenseService.getTotalApprovedExpensesByDateRange(expenseStartDate, expenseEndDate);
        long pendingExpenses = expenseService.countByApprovalStatus(false);
        
        Map<String, Object> dashboard = Map.of(
                "revenue", Map.of(
                    "total", totalRevenue,
                    "paidInvoices", paidInvoices,
                    "pendingInvoices", pendingInvoices,
                    "failedInvoices", failedInvoices
                ),
                "courses", Map.of(
                    "total", totalCourses,
                    "available", availableCourses.size()
                ),
                "users", Map.of(
                    "total", totalUsers,
                    "admins", adminUsers,
                    "newUsers", newUsers
                ),
                "expenses", Map.of(
                    "total", totalExpenses,
                    "pending", pendingExpenses
                ),
                "dateRange", Map.of(
                    "start", startDate,
                    "end", endDate
                )
        );
        
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/revenue")
    @Operation(summary = "Get revenue statistics", description = "Get detailed revenue statistics (Admin only)")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(3);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        BigDecimal totalRevenue = invoiceService.getTotalRevenueByDateRange(startDate, endDate);
        long paidInvoices = invoiceService.countPaidInvoicesByDateRange(startDate, endDate);
        List<Object[]> paymentMethodStats = invoiceService.getPaymentMethodStatistics();
        
        // Calculate monthly revenue (simplified - would be better with dedicated repository methods)
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        BigDecimal monthlyRevenue = invoiceService.getTotalRevenueByDateRange(monthAgo, LocalDateTime.now());
        
        Map<String, Object> revenueStats = Map.of(
                "totalRevenue", totalRevenue,
                "monthlyRevenue", monthlyRevenue,
                "paidInvoices", paidInvoices,
                "paymentMethodBreakdown", paymentMethodStats,
                "dateRange", Map.of("start", startDate, "end", endDate)
        );
        
        return ResponseEntity.ok(revenueStats);
    }
    
    @GetMapping("/courses")
    @Operation(summary = "Get course statistics", description = "Get course statistics (Admin only)")
    public ResponseEntity<Map<String, Object>> getCourseStatistics() {
        long totalCourses = courseService.getAllCourses().size();
        long activeCourses = courseService.countActiveCourses();
        List<String> categories = courseService.getAllCategories();
        List<String> instructors = courseService.getAllInstructors();
        List<Course> availableCourses = courseService.getAvailableCourses();
        
        Map<String, Object> courseStats = Map.of(
                "totalCourses", totalCourses,
                "activeCourses", activeCourses,
                "inactiveCourses", totalCourses - activeCourses,
                "totalCategories", categories.size(),
                "totalInstructors", instructors.size(),
                "availableCourses", availableCourses.size(),
                "categories", categories,
                "instructors", instructors
        );
        
        return ResponseEntity.ok(courseStats);
    }
    
    @GetMapping("/users")
    @Operation(summary = "Get user statistics", description = "Get user statistics (Admin only)")
    public ResponseEntity<Map<String, Object>> getUserStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(3);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        long totalUsers = userService.countUsersByRole(User.Role.USER);
        long adminUsers = userService.countUsersByRole(User.Role.ADMIN);
        long newUsers = userService.countNewUsersBetween(startDate, endDate);
        
        Map<String, Object> userStats = Map.of(
                "totalUsers", totalUsers + adminUsers,
                "regularUsers", totalUsers,
                "adminUsers", adminUsers,
                "newUsers", newUsers,
                "dateRange", Map.of("start", startDate, "end", endDate)
        );
        
        return ResponseEntity.ok(userStats);
    }
    
    @GetMapping("/financial")
    @Operation(summary = "Get financial overview", description = "Get financial overview combining revenue and expenses (Admin only)")
    public ResponseEntity<Map<String, Object>> getFinancialOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        BigDecimal totalRevenue = invoiceService.getTotalRevenueByDateRange(startDate, endDate);
        LocalDate expenseStartDate = startDate.toLocalDate();
        LocalDate expenseEndDate = endDate.toLocalDate();
        BigDecimal totalExpenses = expenseService.getTotalApprovedExpensesByDateRange(expenseStartDate, expenseEndDate);
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);
        
        Map<String, Object> financialOverview = Map.of(
                "totalRevenue", totalRevenue,
                "totalExpenses", totalExpenses,
                "netProfit", netProfit,
                "profitMargin", totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
                    netProfit.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) : 
                    BigDecimal.ZERO,
                "dateRange", Map.of("start", startDate, "end", endDate)
        );
        
        return ResponseEntity.ok(financialOverview);
    }
}
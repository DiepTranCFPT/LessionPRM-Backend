package com.lessionprm.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "revenues", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"month", "year"})
})
@EntityListeners(AuditingEntityListener.class)
public class Revenue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer month;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(name = "total_revenue", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    
    @Column(name = "total_expense", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalExpense = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal profit = BigDecimal.ZERO;
    
    @Column(name = "course_sales_count")
    private Integer courseSalesCount = 0;
    
    @Column(name = "new_users_count")
    private Integer newUsersCount = 0;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Revenue() {}
    
    public Revenue(Integer month, Integer year) {
        this.month = month;
        this.year = year;
        this.totalRevenue = BigDecimal.ZERO;
        this.totalExpense = BigDecimal.ZERO;
        this.profit = BigDecimal.ZERO;
    }
    
    // Helper method to calculate profit
    public void calculateProfit() {
        this.profit = this.totalRevenue.subtract(this.totalExpense);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getMonth() {
        return month;
    }
    
    public void setMonth(Integer month) {
        this.month = month;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
        calculateProfit();
    }
    
    public BigDecimal getTotalExpense() {
        return totalExpense;
    }
    
    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
        calculateProfit();
    }
    
    public BigDecimal getProfit() {
        return profit;
    }
    
    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
    
    public Integer getCourseSalesCount() {
        return courseSalesCount;
    }
    
    public void setCourseSalesCount(Integer courseSalesCount) {
        this.courseSalesCount = courseSalesCount;
    }
    
    public Integer getNewUsersCount() {
        return newUsersCount;
    }
    
    public void setNewUsersCount(Integer newUsersCount) {
        this.newUsersCount = newUsersCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
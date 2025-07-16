package com.lessionprm.service.interfaces;

import com.lessionprm.entity.Revenue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RevenueService {
    
    Revenue createOrUpdateRevenue(Integer month, Integer year);
    
    Optional<Revenue> getRevenueByMonthAndYear(Integer month, Integer year);
    
    List<Revenue> getRevenueByYear(Integer year);
    
    List<Revenue> getRevenueByYearRange(Integer startYear, Integer endYear);
    
    Revenue updateRevenue(Long id, Revenue revenue);
    
    void deleteRevenue(Long id);
    
    BigDecimal getTotalRevenueByYear(Integer year);
    
    BigDecimal getTotalExpenseByYear(Integer year);
    
    BigDecimal getTotalProfitByYear(Integer year);
    
    List<Object[]> getYearlySummary();
    
    List<Revenue> getProfitableMonths(BigDecimal minProfit);
    
    List<Revenue> getLossMonths(BigDecimal maxProfit);
    
    BigDecimal getAverageMonthlyRevenueByYear(Integer year);
    
    BigDecimal getAverageMonthlyProfitByYear(Integer year);
    
    List<Revenue> getAllRevenuesOrderByDate();
    
    long countProfitableMonths();
    
    long countLossMonths();
    
    BigDecimal getMaxMonthlyRevenue();
    
    BigDecimal getMinMonthlyRevenue();
    
    void generateMonthlyRevenue(Integer month, Integer year);
    
    void recalculateAllRevenues();
}
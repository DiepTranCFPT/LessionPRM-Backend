package com.lessionprm.repository;

import com.lessionprm.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    
    Optional<Revenue> findByMonthAndYear(Integer month, Integer year);
    
    List<Revenue> findByYear(Integer year);
    
    List<Revenue> findByYearOrderByMonthAsc(Integer year);
    
    @Query("SELECT r FROM Revenue r WHERE r.year BETWEEN :startYear AND :endYear ORDER BY r.year, r.month")
    List<Revenue> findByYearBetween(@Param("startYear") Integer startYear, 
                                   @Param("endYear") Integer endYear);
    
    @Query("SELECT SUM(r.totalRevenue) FROM Revenue r WHERE r.year = :year")
    BigDecimal getTotalRevenueByYear(@Param("year") Integer year);
    
    @Query("SELECT SUM(r.totalExpense) FROM Revenue r WHERE r.year = :year")
    BigDecimal getTotalExpenseByYear(@Param("year") Integer year);
    
    @Query("SELECT SUM(r.profit) FROM Revenue r WHERE r.year = :year")
    BigDecimal getTotalProfitByYear(@Param("year") Integer year);
    
    @Query("SELECT r.year, SUM(r.totalRevenue), SUM(r.totalExpense), SUM(r.profit) " +
           "FROM Revenue r GROUP BY r.year ORDER BY r.year")
    List<Object[]> getYearlySummary();
    
    @Query("SELECT r FROM Revenue r WHERE r.profit > :minProfit ORDER BY r.profit DESC")
    List<Revenue> findByProfitGreaterThan(@Param("minProfit") BigDecimal minProfit);
    
    @Query("SELECT r FROM Revenue r WHERE r.profit < :maxProfit ORDER BY r.profit ASC")
    List<Revenue> findByProfitLessThan(@Param("maxProfit") BigDecimal maxProfit);
    
    @Query("SELECT AVG(r.totalRevenue) FROM Revenue r WHERE r.year = :year")
    BigDecimal getAverageMonthlyRevenueByYear(@Param("year") Integer year);
    
    @Query("SELECT AVG(r.profit) FROM Revenue r WHERE r.year = :year")
    BigDecimal getAverageMonthlyProfitByYear(@Param("year") Integer year);
    
    @Query("SELECT r FROM Revenue r ORDER BY r.year DESC, r.month DESC")
    List<Revenue> findAllOrderByYearDescMonthDesc();
    
    @Query("SELECT COUNT(r) FROM Revenue r WHERE r.profit > 0")
    long countProfitableMonths();
    
    @Query("SELECT COUNT(r) FROM Revenue r WHERE r.profit < 0")
    long countLossMonths();
    
    @Query("SELECT MAX(r.totalRevenue) FROM Revenue r")
    BigDecimal getMaxMonthlyRevenue();
    
    @Query("SELECT MIN(r.totalRevenue) FROM Revenue r WHERE r.totalRevenue > 0")
    BigDecimal getMinMonthlyRevenue();
}
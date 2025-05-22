package com.fintech.service;

import com.fintech.dto.*;
import com.fintech.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReportsService {

    @Autowired
    private TransactionRepository transactionRepository;

    public CashflowReportDto getCashflowReport(UUID userId, LocalDate fromDate, LocalDate toDate) {
        CashflowReportDto report = new CashflowReportDto();
        report.setFromDate(fromDate);
        report.setToDate(toDate);

        // Get total income and expenses
        BigDecimal totalIncome = transactionRepository.calculateTotalIncomeByUserAndDateRange(userId, fromDate, toDate);
        BigDecimal totalExpenses = transactionRepository.calculateTotalExpensesByUserAndDateRange(userId, fromDate, toDate);

        report.setTotalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO);
        report.setTotalExpenses(totalExpenses != null ? totalExpenses : BigDecimal.ZERO);
        report.setNetCashflow(report.getTotalIncome().subtract(report.getTotalExpenses()));

        // Get daily data points
        List<CashflowReportDto.CashflowDataPoint> dataPoints = transactionRepository.getDailyCashflowData(userId, fromDate, toDate);
        report.setDataPoints(dataPoints);

        return report;
    }

    public SpendByCategoryReportDto getSpendByCategoryReport(UUID userId, LocalDate fromDate, LocalDate toDate) {
        SpendByCategoryReportDto report = new SpendByCategoryReportDto();
        report.setFromDate(fromDate);
        report.setToDate(toDate);

        // Get category spend data
        List<SpendByCategoryReportDto.CategorySpendData> categoryData = 
                transactionRepository.getSpendByCategoryData(userId, fromDate, toDate);

        // Calculate total spent
        BigDecimal totalSpent = categoryData.stream()
                .map(SpendByCategoryReportDto.CategorySpendData::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        report.setTotalSpent(totalSpent);

        // Calculate percentages
        if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
            for (SpendByCategoryReportDto.CategorySpendData data : categoryData) {
                BigDecimal percentage = data.getAmount()
                        .divide(totalSpent, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                data.setPercentage(percentage);
            }
        }

        report.setCategoryData(categoryData);
        return report;
    }

    public TrendReportDto getTrendReport(UUID userId, int months) {
        TrendReportDto report = new TrendReportDto();
        report.setMonths(months);

        // Calculate date range
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months - 1).withDayOfMonth(1);

        report.setStartDate(startDate);
        report.setEndDate(endDate);

        // Get monthly trend data
        List<TrendReportDto.MonthlyTrendData> monthlyData = 
                transactionRepository.getMonthlyTrendData(userId, startDate, endDate);

        report.setMonthlyData(monthlyData);

        // Calculate summary
        TrendReportDto.TrendSummary summary = calculateTrendSummary(monthlyData);
        report.setSummary(summary);

        return report;
    }

    private TrendReportDto.TrendSummary calculateTrendSummary(List<TrendReportDto.MonthlyTrendData> monthlyData) {
        TrendReportDto.TrendSummary summary = new TrendReportDto.TrendSummary();

        if (monthlyData.isEmpty()) {
            summary.setAverageIncome(BigDecimal.ZERO);
            summary.setAverageExpenses(BigDecimal.ZERO);
            summary.setAverageNetCashflow(BigDecimal.ZERO);
            summary.setTotalIncome(BigDecimal.ZERO);
            summary.setTotalExpenses(BigDecimal.ZERO);
            summary.setTotalNetCashflow(BigDecimal.ZERO);
            summary.setIncomeGrowthRate(BigDecimal.ZERO);
            summary.setExpenseGrowthRate(BigDecimal.ZERO);
            return summary;
        }

        // Calculate totals
        BigDecimal totalIncome = monthlyData.stream()
                .map(TrendReportDto.MonthlyTrendData::getTotalIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = monthlyData.stream()
                .map(TrendReportDto.MonthlyTrendData::getTotalExpenses)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNetCashflow = monthlyData.stream()
                .map(TrendReportDto.MonthlyTrendData::getNetCashflow)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.setTotalIncome(totalIncome);
        summary.setTotalExpenses(totalExpenses);
        summary.setTotalNetCashflow(totalNetCashflow);

        // Calculate averages
        int monthCount = monthlyData.size();
        summary.setAverageIncome(totalIncome.divide(new BigDecimal(monthCount), 2, RoundingMode.HALF_UP));
        summary.setAverageExpenses(totalExpenses.divide(new BigDecimal(monthCount), 2, RoundingMode.HALF_UP));
        summary.setAverageNetCashflow(totalNetCashflow.divide(new BigDecimal(monthCount), 2, RoundingMode.HALF_UP));

        // Calculate growth rates
        if (monthlyData.size() >= 2) {
            TrendReportDto.MonthlyTrendData firstMonth = monthlyData.get(0);
            TrendReportDto.MonthlyTrendData lastMonth = monthlyData.get(monthlyData.size() - 1);

            BigDecimal incomeGrowthRate = calculateGrowthRate(firstMonth.getTotalIncome(), lastMonth.getTotalIncome());
            BigDecimal expenseGrowthRate = calculateGrowthRate(firstMonth.getTotalExpenses(), lastMonth.getTotalExpenses());

            summary.setIncomeGrowthRate(incomeGrowthRate);
            summary.setExpenseGrowthRate(expenseGrowthRate);
        } else {
            summary.setIncomeGrowthRate(BigDecimal.ZERO);
            summary.setExpenseGrowthRate(BigDecimal.ZERO);
        }

        return summary;
    }

    private BigDecimal calculateGrowthRate(BigDecimal firstValue, BigDecimal lastValue) {
        if (firstValue.compareTo(BigDecimal.ZERO) == 0) {
            return lastValue.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
        }

        BigDecimal growth = lastValue.subtract(firstValue)
                .divide(firstValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return growth;
    }
}

package com.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TrendReportDto {
    private int months;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MonthlyTrendData> monthlyData;
    private TrendSummary summary;

    // Getters and Setters
    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<MonthlyTrendData> getMonthlyData() {
        return monthlyData;
    }

    public void setMonthlyData(List<MonthlyTrendData> monthlyData) {
        this.monthlyData = monthlyData;
    }

    public TrendSummary getSummary() {
        return summary;
    }

    public void setSummary(TrendSummary summary) {
        this.summary = summary;
    }

    public static class MonthlyTrendData {
        private String month;
        private LocalDate monthStart;
        private LocalDate monthEnd;
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netCashflow;
        private int transactionCount;

        // Getters and Setters
        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public LocalDate getMonthStart() {
            return monthStart;
        }

        public void setMonthStart(LocalDate monthStart) {
            this.monthStart = monthStart;
        }

        public LocalDate getMonthEnd() {
            return monthEnd;
        }

        public void setMonthEnd(LocalDate monthEnd) {
            this.monthEnd = monthEnd;
        }

        public BigDecimal getTotalIncome() {
            return totalIncome;
        }

        public void setTotalIncome(BigDecimal totalIncome) {
            this.totalIncome = totalIncome;
        }

        public BigDecimal getTotalExpenses() {
            return totalExpenses;
        }

        public void setTotalExpenses(BigDecimal totalExpenses) {
            this.totalExpenses = totalExpenses;
        }

        public BigDecimal getNetCashflow() {
            return netCashflow;
        }

        public void setNetCashflow(BigDecimal netCashflow) {
            this.netCashflow = netCashflow;
        }

        public int getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(int transactionCount) {
            this.transactionCount = transactionCount;
        }
    }

    public static class TrendSummary {
        private BigDecimal averageIncome;
        private BigDecimal averageExpenses;
        private BigDecimal averageNetCashflow;
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal totalNetCashflow;
        private BigDecimal incomeGrowthRate;
        private BigDecimal expenseGrowthRate;

        // Getters and Setters
        public BigDecimal getAverageIncome() {
            return averageIncome;
        }

        public void setAverageIncome(BigDecimal averageIncome) {
            this.averageIncome = averageIncome;
        }

        public BigDecimal getAverageExpenses() {
            return averageExpenses;
        }

        public void setAverageExpenses(BigDecimal averageExpenses) {
            this.averageExpenses = averageExpenses;
        }

        public BigDecimal getAverageNetCashflow() {
            return averageNetCashflow;
        }

        public void setAverageNetCashflow(BigDecimal averageNetCashflow) {
            this.averageNetCashflow = averageNetCashflow;
        }

        public BigDecimal getTotalIncome() {
            return totalIncome;
        }

        public void setTotalIncome(BigDecimal totalIncome) {
            this.totalIncome = totalIncome;
        }

        public BigDecimal getTotalExpenses() {
            return totalExpenses;
        }

        public void setTotalExpenses(BigDecimal totalExpenses) {
            this.totalExpenses = totalExpenses;
        }

        public BigDecimal getTotalNetCashflow() {
            return totalNetCashflow;
        }

        public void setTotalNetCashflow(BigDecimal totalNetCashflow) {
            this.totalNetCashflow = totalNetCashflow;
        }

        public BigDecimal getIncomeGrowthRate() {
            return incomeGrowthRate;
        }

        public void setIncomeGrowthRate(BigDecimal incomeGrowthRate) {
            this.incomeGrowthRate = incomeGrowthRate;
        }

        public BigDecimal getExpenseGrowthRate() {
            return expenseGrowthRate;
        }

        public void setExpenseGrowthRate(BigDecimal expenseGrowthRate) {
            this.expenseGrowthRate = expenseGrowthRate;
        }
    }
}

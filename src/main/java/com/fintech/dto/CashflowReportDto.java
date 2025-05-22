package com.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CashflowReportDto {
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netCashflow;
    private List<CashflowDataPoint> dataPoints;

    // Getters and Setters
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
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

    public List<CashflowDataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<CashflowDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public static class CashflowDataPoint {
        private LocalDate date;
        private BigDecimal income;
        private BigDecimal expenses;
        private BigDecimal netCashflow;

        // Getters and Setters
        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public BigDecimal getIncome() {
            return income;
        }

        public void setIncome(BigDecimal income) {
            this.income = income;
        }

        public BigDecimal getExpenses() {
            return expenses;
        }

        public void setExpenses(BigDecimal expenses) {
            this.expenses = expenses;
        }

        public BigDecimal getNetCashflow() {
            return netCashflow;
        }

        public void setNetCashflow(BigDecimal netCashflow) {
            this.netCashflow = netCashflow;
        }
    }
}

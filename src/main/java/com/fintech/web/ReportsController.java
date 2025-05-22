package com.fintech.web;

import com.fintech.dto.*;
import com.fintech.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/cashflow")
    public ResponseEntity<CashflowReportDto> getCashflowReport(
            @RequestParam UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        CashflowReportDto report = reportsService.getCashflowReport(userId, from, to);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/spend-by-category")
    public ResponseEntity<SpendByCategoryReportDto> getSpendByCategoryReport(
            @RequestParam UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        SpendByCategoryReportDto report = reportsService.getSpendByCategoryReport(userId, from, to);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/trend")
    public ResponseEntity<TrendReportDto> getTrendReport(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "6") int months) {
        TrendReportDto report = reportsService.getTrendReport(userId, months);
        return ResponseEntity.ok(report);
    }
}

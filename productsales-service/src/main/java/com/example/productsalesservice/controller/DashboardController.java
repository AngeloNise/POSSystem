package com.example.productsalesservice.controller;

import com.code.share.codesharing.model.ResultPage;
import com.example.productsalesservice.models.MetricDto;
import com.example.productsalesservice.models.RecentPurchaseDto;
import com.example.productsalesservice.models.StockDto;
import com.example.productsalesservice.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("/metrics")
    public MetricDto getMetricsByPeriod(@RequestBody Map<String, String> request) {
        String period = request.get("period");

        switch (period.toLowerCase()) {
            case "today":
                return dashboardService.getTodayMetrics();
            case "week":
                return dashboardService.getWeeklyMetrics();
            case "month":
                return dashboardService.getMonthlyMetrics();
            case "quarter":
                return dashboardService.getQuarterlyMetrics();
            case "year":
                return dashboardService.getAnnualMetrics();
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }
    }

    @PostMapping("/recentlist")
    public ResultPage<RecentPurchaseDto> getRecentPurchaseList(@RequestBody Map<String, Object> params) {
        return dashboardService.getRecentPurchaseList(params);
    }

    @PostMapping("/list")
    public ResultPage<StockDto> stockList(@RequestBody Map<String, Object> params) {
        // Ensure params are being passed and used correctly
        return dashboardService.stockList(params);
    }

}

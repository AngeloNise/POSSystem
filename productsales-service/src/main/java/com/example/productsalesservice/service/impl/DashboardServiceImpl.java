package com.example.productsalesservice.service.impl;

import com.code.share.codesharing.model.ResultPage;
import com.example.productsalesservice.models.MetricDto;
import com.example.productsalesservice.models.RecentPurchaseDto;
import com.example.productsalesservice.models.StockDto;
import com.example.productsalesservice.repository.DashboardRepository;
import com.example.productsalesservice.service.DashboardService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Override
    public MetricDto getTodayMetrics() {
        return dashboardRepository.getTodayMetrics();
    }

    @Override
    public MetricDto getWeeklyMetrics() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(6); // includes today
        Map<String, Object> params = Map.of(
                "startDate", startOfWeek,
                "endDate", today
        );
        return dashboardRepository.getMetricsBetween(params);
    }

    @Override
    public MetricDto getMonthlyMetrics() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        Map<String, Object> params = Map.of(
                "startDate", startOfMonth,
                "endDate", today
        );
        return dashboardRepository.getMetricsBetween(params);
    }

    @Override
    public MetricDto getQuarterlyMetrics() {
        LocalDate today = LocalDate.now();
        int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
        LocalDate startOfQuarter = LocalDate.of(today.getYear(), (currentQuarter - 1) * 3 + 1, 1);
        Map<String, Object> params = Map.of(
                "startDate", startOfQuarter,
                "endDate", today
        );
        return dashboardRepository.getMetricsBetween(params);
    }

    @Override
    public MetricDto getAnnualMetrics() {
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.withDayOfYear(1);
        Map<String, Object> params = Map.of(
                "startDate", startOfYear,
                "endDate", today
        );
        return dashboardRepository.getMetricsBetween(params);
    }

    @Override
    public ResultPage<RecentPurchaseDto> getRecentPurchaseList(Map<String, Object> params) {
        boolean hasSearchFilters = params.containsKey("receiptNumber") || params.containsKey("productName") ||
                params.containsKey("counter") || params.containsKey("createdBy");

        if (!hasSearchFilters) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            params.put("createdDate", today);
        }

        Integer start = MapUtils.getInteger(params, "start", 0);
        Integer limit = MapUtils.getInteger(params, "limit", 10);

        List<RecentPurchaseDto> recentPurchases = dashboardRepository.getRecentPurchases(params);

        int totalCount = (start == 1 && recentPurchases.size() < limit)
                ? recentPurchases.size()
                : dashboardRepository.getTotalRecentPurchaseCount(params);

        return new ResultPage<>(recentPurchases, totalCount, limit, start);
    }

    @Override
    public ResultPage<StockDto> stockList(Map<String, Object> params) {
        Integer start = MapUtils.getInteger(params, "start", 0);
        Integer limit = MapUtils.getInteger(params, "limit", 10);
        List<StockDto> stocks = dashboardRepository.stockList(params);
        int totalCount = start == 1 && stocks.size() < limit
                ? stocks.size() : dashboardRepository.getTotalStockCount(params);
        return new ResultPage<>(stocks, totalCount, limit, start);
    }

}

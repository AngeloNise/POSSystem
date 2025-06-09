package com.example.productsalesservice.service;

import com.code.share.codesharing.model.ResultPage;
import com.example.productsalesservice.models.MetricDto;
import com.example.productsalesservice.models.RecentPurchaseDto;
import com.example.productsalesservice.models.StockDto;

import java.util.Map;

public interface DashboardService {

    MetricDto getTodayMetrics();

    MetricDto getWeeklyMetrics();

    MetricDto getMonthlyMetrics();

    MetricDto getQuarterlyMetrics();

    MetricDto getAnnualMetrics();

    ResultPage<RecentPurchaseDto> getRecentPurchaseList(Map<String, Object> params);

    ResultPage<StockDto> stockList(Map<String, Object> params);
}

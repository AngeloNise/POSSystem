package com.example.productsalesservice.repository;

import com.example.productsalesservice.models.MetricDto;
import com.example.productsalesservice.models.RecentPurchaseDto;
import com.example.productsalesservice.models.StockDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DashboardRepository {
    MetricDto getTodayMetrics();

    MetricDto getMetricsBetween(Map<String, Object> params);

    List<RecentPurchaseDto> getRecentPurchases(Map<String, Object> params);

    int getTotalRecentPurchaseCount(Map<String, Object> params);

    List<StockDto> stockList(Map<String, Object> params);

    int getTotalStockCount(Map<String, Object> params);
}

package com.example.inventoryservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.inventoryservice.models.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}


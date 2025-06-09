package com.microservice.systemservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.microservice.systemservice.models.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}


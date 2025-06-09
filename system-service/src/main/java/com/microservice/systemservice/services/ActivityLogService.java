package com.microservice.systemservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microservice.systemservice.models.ActivityLog;
import org.apache.ibatis.session.SqlSession;
import java.util.Date;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private final SqlSession sqlSession;

    @Autowired
    public ActivityLogService(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public void logActivity(String pageName, String action, String createdById, String createdByName) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setPageName(pageName);
        activityLog.setAction(action);
        activityLog.setCreatedDate(new Date());
        activityLog.setCreatedById(createdById);
        activityLog.setCreatedByName(createdByName);

        sqlSession.insert("insertActivityLog", activityLog);
    }

    public void logActivity(String pageName, String action, String createdByName) {
    }
}

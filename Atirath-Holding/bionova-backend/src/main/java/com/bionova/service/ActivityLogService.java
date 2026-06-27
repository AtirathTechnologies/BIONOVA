package com.bionova.service;

import com.bionova.entity.ActivityLog;
import com.bionova.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logStatusChange(String entityTyp, Long entityId, String statusFrom, String statusTo) {
        ActivityLog log = new ActivityLog();
        log.setEntityTyp(entityTyp);
        log.setEntityId(entityId);
        log.setStatusFrom(statusFrom != null ? statusFrom : "N/A");
        log.setStatusTo(statusTo);
        log.setLogDt(LocalDateTime.now());
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getLogsForEntity(String entityTyp, Long entityId) {
        return activityLogRepository.findByEntityTypAndEntityId(entityTyp, entityId);
    }

    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findAll();
    }
}

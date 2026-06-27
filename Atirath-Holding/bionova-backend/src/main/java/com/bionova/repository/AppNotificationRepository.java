package com.bionova.repository;

import com.bionova.entity.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByEmpIdAndIsReadFalse(Long empId);
    List<AppNotification> findByEmpIdOrderByCreatedAtDesc(Long empId);
}

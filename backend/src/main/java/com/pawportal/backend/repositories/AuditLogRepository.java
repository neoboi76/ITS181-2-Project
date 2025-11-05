package com.pawportal.backend.repositories;

import com.pawportal.backend.models.AuditLogModel;
import com.pawportal.backend.models.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogModel, Long> {

    List<AuditLogModel> findByUserUserId(Long userId);

    List<AuditLogModel> findByAction(AuditAction action);

    List<AuditLogModel> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AuditLogModel> findTop100ByOrderByTimestampDesc();
}
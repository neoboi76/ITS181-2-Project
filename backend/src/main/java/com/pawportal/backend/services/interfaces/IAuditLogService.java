package com.pawportal.backend.services.interfaces;

import com.pawportal.backend.models.AuditLogModel;
import com.pawportal.backend.models.UserModel;
import com.pawportal.backend.models.enums.AuditAction;

import java.util.List;

public interface IAuditLogService {

    void logAction(UserModel user, AuditAction action, String ipAddress, String userAgent, String details);

    void logAction(Long userId, AuditAction action, String ipAddress, String userAgent, String details);

    void logActionWithError(UserModel user, AuditAction action, String ipAddress, String userAgent, String details, String errorMessage);

    List<AuditLogModel> getAllAuditLogs();

    List<AuditLogModel> getAuditLogsByUserId(Long userId);

    List<AuditLogModel> getAuditLogsByAction(AuditAction action);

    List<AuditLogModel> getRecentAuditLogs(int limit);
}
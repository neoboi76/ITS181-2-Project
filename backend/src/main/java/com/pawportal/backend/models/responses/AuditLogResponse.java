package com.pawportal.backend.models.responses;

import com.pawportal.backend.models.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogResponse {
    private Long auditId;
    private UserAuditInfo user;
    private AuditAction action;
    private String ipAddress;
    private String userAgent;
    private String details;
    private LocalDateTime timestamp;
    private Boolean success;
    private String errorMessage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAuditInfo {
        private Long userId;
        private String email;
        private String firstName;
        private String lastName;
    }
}
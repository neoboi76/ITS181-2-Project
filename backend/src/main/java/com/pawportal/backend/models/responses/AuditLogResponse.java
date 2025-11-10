package com.pawportal.backend.models.responses;

import com.pawportal.backend.models.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Developed by Group 6:
 * Kenji Mark Alan Arceo
 * Carl Norbi Felonia
 * Ryonan Owen Ferrer
 * Dino Alfred Timbol
 * Mike Emil Vocal
 */

/**
 * DTO (Data Transfer Object) response class for audit log response.
 */


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
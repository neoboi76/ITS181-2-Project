package com.pawportal.backend.controllers;

import com.pawportal.backend.models.enums.AuditAction;
import com.pawportal.backend.models.requests.ApplicationFormRequest;
import com.pawportal.backend.models.responses.ApplicationFormResponse;
import com.pawportal.backend.services.implementations.JwtTokenProvider;
import com.pawportal.backend.services.interfaces.IApplicationFormService;
import com.pawportal.backend.services.interfaces.IAuditLogService;
import com.pawportal.backend.services.interfaces.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationFormController {

    private final IApplicationFormService applicationFormService;
    private final IAuditLogService auditLogService;
    private final IAuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<ApplicationFormResponse> createApplication(@RequestBody ApplicationFormRequest application, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);

            ApplicationFormResponse created = applicationFormService.createApplication(application);

            auditLogService.logAction(userId, AuditAction.APPLICATION_CREATED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Created application for dog (request dogId): " + application.getDogId()
                            + " — new application submitted at: " + created.getSubmittedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ApplicationFormResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationFormService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationFormResponse> getApplicationById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);
            auditLogService.logAction(userId, AuditAction.APPLICATION_VIEWED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Viewed application ID: " + id);

            return applicationFormService.getApplicationById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ApplicationFormResponse>> getApplicationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(applicationFormService.getApplicationsByUserId(userId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApplicationFormResponse> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);

            ApplicationFormResponse updated = applicationFormService.updateApplicationStatus(id, status);

            auditLogService.logAction(userId, AuditAction.APPLICATION_UPDATED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Updated application ID: " + id + " to status: " + status);

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}

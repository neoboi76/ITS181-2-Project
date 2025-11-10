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

/** Developed by Group 6:
 * Kenji Mark Alan Arceo
 * Carl Norbi Felonia
 * Ryonan Owen Ferrer
 * Dino Alfred Timbol
 * Mike Emil Vocal
 */

/**
 * Controller class for application form related user and admin
 * operations
 */

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationFormController {

    private final IApplicationFormService applicationFormService;
    private final IAuditLogService auditLogService;
    private final IAuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    //Creates applications (only logged users can do this)
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

    //Admin retrieval of all applications
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ApplicationFormResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationFormService.getAllApplications());
    }

    //Admin retrieval of a particular application
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    //Admin retrieval of all applications sent by a particular user
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ApplicationFormResponse>> getApplicationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(applicationFormService.getApplicationsByUserId(userId));
    }

    //Admin method that can update application status
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

    //Helper method that extracts JWT token form HTTP request
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    //Helper method that extracts user IP address from HTTP request
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

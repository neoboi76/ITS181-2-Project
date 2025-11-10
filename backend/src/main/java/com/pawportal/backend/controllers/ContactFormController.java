package com.pawportal.backend.controllers;

import com.pawportal.backend.models.ContactFormModel;
import com.pawportal.backend.models.enums.AuditAction;
import com.pawportal.backend.services.implementations.JwtTokenProvider;
import com.pawportal.backend.services.interfaces.IContactFormService;
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
 * Controller class for contact form related user and admin
 * operations
 */

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactFormController {

    private final IContactFormService contactFormService;
    private final IAuditLogService auditLogService;
    private final IAuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    //Creates contact form (non-logged users included)
    @PostMapping
    public ResponseEntity<ContactFormModel> createContactForm(@RequestBody ContactFormModel contactForm, HttpServletRequest request) {
        try {
            ContactFormModel created = contactFormService.createContactForm(contactForm);

            String token = getTokenFromRequest(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                Long userId = authService.getUserIdByEmail(email);
                auditLogService.logAction(userId, AuditAction.CONTACT_CREATED,
                        getClientIp(request), request.getHeader("User-Agent"),
                        "Submitted contact form ID: " + created.getContactId());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Admin retrieval of all contact forms sent to the system
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactFormModel>> getAllContactForms(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);
            auditLogService.logAction(userId, AuditAction.CONTACT_VIEWED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Viewed all contact forms");

            return ResponseEntity.ok(contactFormService.getAllContactForms());

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
package com.pawportal.backend.controllers;

import com.pawportal.backend.models.DogModel;
import com.pawportal.backend.models.enums.AuditAction;
import com.pawportal.backend.models.enums.DogStatus;
import com.pawportal.backend.services.implementations.JwtTokenProvider;
import com.pawportal.backend.services.interfaces.IAuditLogService;
import com.pawportal.backend.services.interfaces.IAuthService;
import com.pawportal.backend.services.interfaces.IDogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/dogs")
@RequiredArgsConstructor
public class DogController {

    private final IDogService dogService;
    private final IAuditLogService auditLogService;
    private final IAuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<List<DogModel>> getAllDogs() {
        return ResponseEntity.ok(dogService.getAllDogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DogModel> getDogById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);
            auditLogService.logAction(userId, AuditAction.DOG_VIEWED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Viewed dog ID: " + id);

            return dogService.getDogById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DogModel>> getDogsByStatus(@PathVariable String status) {
        DogStatus dogStatus = DogStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(dogService.getDogsByStatus(dogStatus));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DogModel> createDog(@RequestBody DogModel dog, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);

            DogModel createdDog = dogService.createDog(dog);

            auditLogService.logAction(userId, AuditAction.DOG_CREATED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Created dog: " + createdDog.getName() + " (ID: " + createdDog.getDogId() + ")");

            return ResponseEntity.status(HttpStatus.CREATED).body(createdDog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DogModel> updateDog(@PathVariable Long id, @RequestBody DogModel dog, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);

            DogModel updatedDog = dogService.updateDog(id, dog);

            auditLogService.logAction(userId, AuditAction.DOG_UPDATED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Updated dog: " + updatedDog.getName() + " (ID: " + id + ")");

            return ResponseEntity.ok(updatedDog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDog(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);

            dogService.deleteDog(id);

            auditLogService.logAction(userId, AuditAction.DOG_DELETED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Deleted dog ID: " + id);

            return ResponseEntity.noContent().build();
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
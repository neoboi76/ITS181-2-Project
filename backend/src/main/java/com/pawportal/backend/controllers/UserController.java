package com.pawportal.backend.controllers;

import com.pawportal.backend.models.UserModel;
import com.pawportal.backend.models.enums.AuditAction;
import com.pawportal.backend.models.requests.*;
import com.pawportal.backend.models.responses.*;
import com.pawportal.backend.services.implementations.JwtTokenProvider;
import com.pawportal.backend.services.interfaces.IAuditLogService;
import com.pawportal.backend.services.interfaces.IAuthService;
import com.pawportal.backend.services.interfaces.ILogoutService;
import com.pawportal.backend.services.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Developed by Group 6:
 * Kenji Mark Alan Arceo
 * Carl Norbi Felonia
 * Ryonan Owen Ferrer
 * Dino Alfred Timbol
 * Mike Emil Vocal
 */

/**
 * Controller class for user related user and admin
 * operations
 */

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final IAuthService authService;
    private final ILogoutService logoutService;
    private final IUserService userService;
    private final IAuditLogService auditLogService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    //Login authentication controller endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            //Authenticates email and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            LoginResponse response = authService.login(request);

            Long userId = authService.getUserIdByEmail(request.getEmail());
            auditLogService.logAction(userId, AuditAction.USER_LOGIN,
                    getClientIp(httpRequest), httpRequest.getHeader("User-Agent"),
                    "User logged in successfully");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch(Exception e) {
            auditLogService.logAction((Long) null, AuditAction.LOGIN_FAILED,
                    getClientIp(httpRequest), httpRequest.getHeader("User-Agent"),
                    "Failed login attempt for email: " + request.getEmail());
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    //Sign up endpoint for creation of new users
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        try {
            RegisterResponse response = authService.register(request);

            if (response == null) {
                return ResponseEntity.badRequest().body("Invalid signup");
            }

            auditLogService.logAction(response.getId(), AuditAction.USER_REGISTER,
                    getClientIp(httpRequest), httpRequest.getHeader("User-Agent"),
                    "New user registered: " + request.getEmail());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Register failed: " + e.getMessage());
        }
    }

    //Reset password endpoint that allows password reset from the settings page (user must be logged in)
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetRequest request, HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getOldPassword())
            );

            ResetResponse response = authService.resetPassword(request);

            Long userId = authService.getUserIdByEmail(request.getEmail());
            auditLogService.logAction(userId, AuditAction.PASSWORD_RESET,
                    getClientIp(httpRequest), httpRequest.getHeader("User-Agent"),
                    "Password reset from settings");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password reset failed: " + e.getMessage());
        }
    }

    //Forgot password endpoint that allows password reset login page (if user "forgot" their password)
    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotRequest request, HttpServletRequest httpRequest) {
        try {
            if(authService.isEmailValid(request.getEmail())) {
                ResetResponse response = authService.forgotPassword(request);

                Long userId = authService.getUserIdByEmail(request.getEmail());
                auditLogService.logAction(userId, AuditAction.PASSWORD_FORGOT,
                        getClientIp(httpRequest), httpRequest.getHeader("User-Agent"),
                        "Password reset via forgot password");

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password reset failed: " + e.getMessage());
        }
        return null;
    }

    //Endpoint that allows the user to request a link to be sent to their email that will direct
    //them to forgot password page. Link must have a unique id tied to the user (who must already
    //exists in the database).
    @PostMapping("/request-forgot")
    public ResponseEntity<?> requestForgot(@RequestBody ForgotRequest body, HttpServletRequest request) {
        try {
            String email = body.getEmail();
            ForgotResponse msg = authService.requestForgot(email);
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password reset was unsuccessful");
        }
    }

    //Logout endpoint. This also blacklists the jwt token the moment the user logs out
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);
            logoutService.blacklistToken(token);

            auditLogService.logAction(userId, AuditAction.USER_LOGOUT,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "User logged out");

            return ResponseEntity.ok(new LogoutResponse("Logout successful. Token has been invalidated."));

        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Logout failed " + e.getMessage());
        }
    }

    //Updates user information. Used primarily in the settings page
    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @RequestBody SettingsRequest usrReq) {
        try {
            String token = getTokenFromRequest(request);
            SettingsResponse response = authService.updateUser(usrReq);
            auditLogService.logAction(usrReq.getId(), AuditAction.USER_PROFILE_UPDATED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "User profile updated");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Update user failed: " + e.getMessage());
        }
    }

    //Retrieves a particular user based on their id
    @GetMapping("/get-user/{id}")
    public ResponseEntity<?> getUser(HttpServletRequest request, @PathVariable long id) {
        try {
            String token = getTokenFromRequest(request);
            SettingsRequest response = authService.getUser(id);
            auditLogService.logAction(id, AuditAction.USER_PROFILE_VIEWED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "User profile viewed");

            return ResponseEntity.ok(response);

        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Retrieving user failed " + e.getMessage());
        }
    }

    //Admin method to retrieve a list of users
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //Admin suspension of non-admin users
    @PutMapping("/admin/users/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> suspendUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String adminEmail = jwtTokenProvider.getEmailFromToken(token);
            Long adminId = authService.getUserIdByEmail(adminEmail);

            UserResponse response = userService.suspendUser(id);

            auditLogService.logAction(adminId, AuditAction.USER_SUSPENDED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Admin suspended user ID: " + id);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to suspend user: " + e.getMessage());
        }
    }

    //Admin promotion (to admin) of non-admin users
    @PutMapping("/admin/users/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String adminEmail = jwtTokenProvider.getEmailFromToken(token);
            Long adminId = authService.getUserIdByEmail(adminEmail);
            UserResponse response = userService.promoteUser(id);

            auditLogService.logAction(adminId, AuditAction.USER_PROMOTED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Admin Promoted to Admin user ID: " + id);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to Promote user: " + e.getMessage());
        }
    }

    //Activation of suspended non-admin users
    @PutMapping("/admin/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String adminEmail = jwtTokenProvider.getEmailFromToken(token);
            Long adminId = authService.getUserIdByEmail(adminEmail);

            UserResponse response = userService.activateUser(id);

            auditLogService.logAction(adminId, AuditAction.USER_ACTIVATED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Admin activated user ID: " + id);

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to activate user: " + e.getMessage());
        }
    }

    //Admin deletion of non-admin users
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            String adminEmail = jwtTokenProvider.getEmailFromToken(token);
            Long adminId = authService.getUserIdByEmail(adminEmail);

            userService.deleteUser(id);

            auditLogService.logAction(adminId, AuditAction.USER_DELETED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Admin deleted user ID: " + id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete user: " + e.getMessage());
        }
    }

    //Admin retrieval of all existing audit logs
    @GetMapping("/admin/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAuditLogs(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);

            String email = jwtTokenProvider.getEmailFromToken(token);
            Long userId = authService.getUserIdByEmail(email);

            auditLogService.logAction(userId, AuditAction.SEARCH_PERFORMED,
                    getClientIp(request), request.getHeader("User-Agent"),
                    "Admin viewed audit logs");

            return ResponseEntity.ok(auditLogService.getAllAuditLogs());


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve audit logs: " + e.getMessage());
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
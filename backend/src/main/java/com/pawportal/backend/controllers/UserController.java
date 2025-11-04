package com.pawportal.backend.controllers;

import com.pawportal.backend.models.requests.*;
import com.pawportal.backend.models.responses.*;
import com.pawportal.backend.services.implementations.JwtTokenProvider;
import com.pawportal.backend.services.interfaces.IAuthService;
import com.pawportal.backend.services.interfaces.ILogoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequiredArgsConstructor
public class UserController {

    private final IAuthService authService;
    private final ILogoutService logoutService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken
                            (request.getEmail(), request.getPassword())
            );

            LoginResponse response = authService.login(request);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);


        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }


    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        try {
            RegisterResponse response = authService.register(request);

            if (response == null) {
                return ResponseEntity.badRequest().body("Invalid signup");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Register failed: " + e.getMessage());
        }

    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getOldPassword())
            );

            ResetResponse response = authService.resetPassword(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password reset failed: " + e.getMessage());
        }
    }

    //Handles forgot-password operations and logging
    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotRequest request, HttpServletRequest httpRequest) {
        try {

            if(authService.isEmailValid(request.getEmail())) {
                ResetResponse response = authService.forgotPassword(request);

                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password reset failed: " + e.getMessage());
        }

        return null;
    }

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody ResetRequest body, HttpServletRequest request) {
        try {
            String email = body.getEmail();

            ResetResponse msg = authService.requestReset(email);

            return ResponseEntity.ok(msg);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password reset was unsuccessful");
        }

    }

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            logoutService.blacklistToken(token);
            return ResponseEntity.ok(new LogoutResponse("Logout successful. Token has been invalidated."));

        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Logout failed " + e.getMessage());
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @RequestBody SettingsRequest usrReq) {

        try {

            if (this.jwtTokenProvider.validateToken(getTokenFromRequest(request))) {

                SettingsResponse response = authService.updateUser(usrReq);

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }


        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Update user failed: " + e.getMessage());
        }

        return null;
    }

    @GetMapping("/get-user/{id}")
    public ResponseEntity<?> getUser(HttpServletRequest request, @PathVariable long id) {

        try {

            if (this.jwtTokenProvider.validateToken(getTokenFromRequest(request))) {

                SettingsRequest response = authService.getUser(id);

                return ResponseEntity.ok(response);
            }


        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Retrieving user failed " + e.getMessage());
        }

        return null;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


}
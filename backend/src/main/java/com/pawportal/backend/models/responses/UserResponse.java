package com.pawportal.backend.models.responses;

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
 * DTO (Data Transfer Object) response class for user response response.
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String gender;
    private String country;
    private String language;
    private String role;
    private Boolean suspended;
    private LocalDateTime createdAt;
    private int applicationCount;
}
package com.pawportal.backend.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Developed by Group 6:
 * Kenji Mark Alan Arceo
 * Carl Norbi Felonia
 * Ryonan Owen Ferrer
 * Dino Alfred Timbol
 * Mike Emil Vocal
 */

/**
 * DTO (Data Transfer Object) response class for application form response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationFormResponse {
    private Long ApplicationId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private String occupation;
    private String livingStatus;
    private String reasonForAdoption;
    private String experience;
    private String status;
    private String submittedAt;
    private String adminNotes;
}

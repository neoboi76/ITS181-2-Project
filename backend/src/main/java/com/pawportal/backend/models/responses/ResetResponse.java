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
 * DTO (Data Transfer Object) response class for reset password response.
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetResponse {
    private String message;
}

package com.pawportal.backend.models;

import com.pawportal.backend.models.enums.ApplicationStatus;
import jakarta.persistence.*;
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
 * Main entity class (table) for the internal Model representation.
 * of the application forms
 */


@Entity
@Table(name = "application_forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFormModel {

    //PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    //FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserModel user;


    //FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dogId")
    private DogModel dog;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String occupation;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String livingStatus;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reasonForAdoption;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String experience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String adminNotes;
}
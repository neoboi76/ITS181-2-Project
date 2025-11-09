package com.pawportal.backend.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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
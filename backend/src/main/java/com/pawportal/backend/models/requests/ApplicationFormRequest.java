package com.pawportal.backend.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFormRequest {
    private Long applicationId;
    private Long dogId;
    private Long userId;
    private String status;
    private String submittedAt;
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

}

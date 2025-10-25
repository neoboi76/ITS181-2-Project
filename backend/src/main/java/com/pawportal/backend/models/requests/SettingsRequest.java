package com.pawportal.backend.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingsRequest {
    private String firstName;
    private String lastName;
    private long id;
    private String gender;
    private String country;
    private String language;
}

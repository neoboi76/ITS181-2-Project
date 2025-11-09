package com.pawportal.backend.services.interfaces;

import com.pawportal.backend.models.ApplicationFormModel;
import com.pawportal.backend.models.requests.ApplicationFormRequest;
import com.pawportal.backend.models.responses.ApplicationFormResponse;

import java.util.List;
import java.util.Optional;

public interface IApplicationFormService {
    ApplicationFormResponse createApplication(ApplicationFormRequest application);
    List<ApplicationFormResponse> getAllApplications();
    Optional<ApplicationFormResponse> getApplicationById(Long id);
    List<ApplicationFormResponse> getApplicationsByUserId(Long userId);
    ApplicationFormResponse updateApplicationStatus(Long id, String statusStr);
    ApplicationFormResponse convertToDto(ApplicationFormModel model);
}


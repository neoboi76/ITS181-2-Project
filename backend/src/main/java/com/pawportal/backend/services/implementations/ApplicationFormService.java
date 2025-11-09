package com.pawportal.backend.services.implementations;

import com.pawportal.backend.models.ApplicationFormModel;
import com.pawportal.backend.models.DogModel;
import com.pawportal.backend.models.UserModel;
import com.pawportal.backend.models.enums.ApplicationStatus;
import com.pawportal.backend.models.requests.ApplicationFormRequest;
import com.pawportal.backend.models.responses.ApplicationFormResponse;
import com.pawportal.backend.repositories.ApplicationFormRepository;
import com.pawportal.backend.repositories.DogRepository;
import com.pawportal.backend.repositories.UserRepository;
import com.pawportal.backend.services.interfaces.IApplicationFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationFormService implements IApplicationFormService {

    private final ApplicationFormRepository applicationFormRepository;
    private final DogRepository dogRepository;
    private final UserRepository userRepository;

    @Override
    public ApplicationFormResponse createApplication(ApplicationFormRequest application) {

        ApplicationFormModel request = new ApplicationFormModel();

        DogModel dog = dogRepository.findById(application.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found"));

        UserModel user= userRepository.findById(application.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        request.setDog(dog);
        request.setUser(user);
        request.setFirstName(application.getFirstName());
        request.setLastName(application.getLastName());
        request.setEmail(application.getEmail());
        request.setPhone(application.getPhone());
        request.setAddress(application.getAddress());
        request.setCity(application.getCity());
        request.setPostalCode(application.getPostalCode());
        request.setOccupation(application.getOccupation());
        request.setLivingStatus(application.getLivingStatus());
        request.setReasonForAdoption(application.getReasonForAdoption());
        request.setExperience(application.getExperience());

        ApplicationFormResponse response = convertToDto(applicationFormRepository.save(request));

        return response;
    }

    public List<ApplicationFormResponse> getAllApplications() {
        return applicationFormRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ApplicationFormResponse> getApplicationById(Long id) {
        return applicationFormRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public List<ApplicationFormResponse> getApplicationsByUserId(Long userId) {
        return applicationFormRepository.findByUserUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationFormResponse updateApplicationStatus(Long id, String statusStr) {
        ApplicationFormModel application = applicationFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        ApplicationStatus status = ApplicationStatus.valueOf(statusStr.toUpperCase());
        application.setStatus(status);

        ApplicationFormModel saved = applicationFormRepository.save(application);
        return convertToDto(saved);
    }

    @Override
    public ApplicationFormResponse convertToDto(ApplicationFormModel model) {
        if (model == null) return null;

        ApplicationFormResponse dto = new ApplicationFormResponse();
        dto.setApplicationId(model.getApplicationId());
        dto.setFirstName(model.getFirstName());
        dto.setLastName(model.getLastName());
        dto.setEmail(model.getEmail());
        dto.setPhone(model.getPhone());
        dto.setAddress(model.getAddress());
        dto.setCity(model.getCity());
        dto.setPostalCode(model.getPostalCode());
        dto.setOccupation(model.getOccupation());
        dto.setLivingStatus(model.getLivingStatus());
        dto.setReasonForAdoption(model.getReasonForAdoption());
        dto.setExperience(model.getExperience());
        dto.setStatus(model.getStatus() != null ? model.getStatus().name() : null);
        dto.setSubmittedAt(model.getSubmittedAt() != null ? model.getSubmittedAt().toString() : null);
        dto.setAdminNotes(model.getAdminNotes());

        return dto;
    }


}

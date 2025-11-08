package com.pawportal.backend.services.implementations;

import com.pawportal.backend.models.UserModel;
import com.pawportal.backend.models.enums.Role;
import com.pawportal.backend.models.responses.UserResponse;
import com.pawportal.backend.repositories.UserRepository;
import com.pawportal.backend.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse suspendUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setSuspended(true);
        UserModel savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse activateUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setSuspended(false);
        UserModel savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Cannot delete admin users");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserResponse promoteUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Cannot promote admin users");
        }

        user.setRole(Role.ADMIN);

        UserModel promotedUser = userRepository.save(user);

        return convertToUserResponse(promotedUser);

    }

    private UserResponse convertToUserResponse(UserModel user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setMobileNumber(user.getMobileNumber());
        response.setGender(user.getGender());
        response.setCountry(user.getCountry());
        response.setLanguage(user.getLanguage());
        response.setRole(user.getRole().name());
        response.setSuspended(user.getSuspended());
        response.setCreatedAt(user.getCreatedAt());
        response.setApplicationCount(user.getApplications() != null ? user.getApplications().size() : 0);
        return response;
    }
}